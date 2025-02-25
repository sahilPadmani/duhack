package tech.duhacks.duhacks.schedular;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import tech.duhacks.duhacks.model.HealthProduct;
import tech.duhacks.duhacks.service.MailSenderService;

import java.lang.foreign.MemorySegment;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Getter
@Setter
@AllArgsConstructor
class medicineData{
    private LocalDate expiryTime;
    private Long id;
};


@Component
@Scope("singleton")
public class ExpiryEmail implements Runnable{

    private final MailSenderService mailSender;
    private static final ZoneId kolkataZoneId = ZoneId.of("Asia/Kolkata");
    private static final PriorityQueue<medicineData> taskQueue = new PriorityQueue<>((user1, user2) -> user1.getExpiryTime().compareTo(user2.getExpiryTime()));

    private static final Lock lock = new ReentrantLock();
    private static Thread thread;


    public ExpiryEmail(MailSenderService mailSender) {
        this.mailSender = mailSender;
    }

    public void addMedicine(HealthProduct hp){

        ZonedDateTime kolkataZonedTime = ZonedDateTime.now(kolkataZoneId);
        LocalDate kolkataLocalTime = kolkataZonedTime.toLocalDate();

        var md = new medicineData(hp.getExpiryDate(),hp.getId());

        if(kolkataLocalTime.isAfter(md.getExpiryTime())){
            return;
        }

        lock.lock();
        try{
            if (taskQueue.isEmpty()) {
                taskQueue.add(md);
                thread = new Thread(this);
                thread.start();
                return;
            }

            var firstData = taskQueue.peek();
            if (firstData.getExpiryTime().equals(md.getExpiryTime())
                    || firstData.getExpiryTime().isAfter(md.getExpiryTime())) {
                taskQueue.add(md);
                thread.interrupt();
                return;
            }

            taskQueue.add(md);

        } finally {
            lock.unlock();
        }
    }

    public void removeMedicine(Long id){
        lock.lock();
        try{
            taskQueue.removeIf(e -> e.getId().equals(id));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void run() {
        while (!taskQueue.isEmpty()) {

            medicineData expiryData = taskQueue.peek();
            ZonedDateTime kolkataZonedTime = ZonedDateTime.now(kolkataZoneId);

            long timeForSleep = java.time.Duration.between(kolkataZonedTime, expiryData.getExpiryTime().atStartOfDay(kolkataZoneId).minusDays(2)).toMillis();
            System.out.println(timeForSleep);
            if (timeForSleep <= 0) {
                System.out.println(timeForSleep);
                taskQueue.poll();
                mailSender.SendEmail(expiryData.getId());
            } else {
                try {
                    System.out.println(timeForSleep);
                    thread.sleep(timeForSleep);
                } catch (InterruptedException _) {
                }
            }

        }
    }
}

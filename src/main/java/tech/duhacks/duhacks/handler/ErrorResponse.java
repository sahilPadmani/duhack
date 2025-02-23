package tech.duhacks.duhacks.handler;

import java.util.Map;

public record ErrorResponse(
        Map<String,String> errors
) {
}




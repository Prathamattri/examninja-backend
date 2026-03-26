package in.prathamattri.examninja.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter @Setter
@AllArgsConstructor
public class ErrorResponse {
    private String statusMessage;
    private Map<String, List<String>> errors;
}

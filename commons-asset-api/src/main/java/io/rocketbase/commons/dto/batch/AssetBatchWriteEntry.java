package io.rocketbase.commons.dto.batch;


import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.*;
import org.hibernate.validator.constraints.URL;
import org.springframework.context.annotation.Bean;

import javax.validation.constraints.NotNull;
import java.util.Set;


@Data
@AllArgsConstructor(onConstructor = @_(@JsonCreator))
public class AssetBatchWriteEntry {

    @NotNull
    @URL
    private String url;

    private String systemRefId;

}

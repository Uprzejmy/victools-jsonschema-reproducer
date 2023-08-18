package victools.jsonschema.reproducer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.victools.jsonschema.generator.*;

import org.json.JSONObject;

import java.util.EnumMap;
import java.util.Map;

enum UnitName {
    farmer, blacksmith
}

enum ResourceName {
    wood, stone, food
}

class UnitTrainingCost {
    @JsonProperty
    Map<ResourceName, Integer> trainingCosts = new EnumMap<>(ResourceName.class);

    public UnitTrainingCost() {
        trainingCosts.put(ResourceName.wood, 100);
        trainingCosts.put(ResourceName.food, 150);
        trainingCosts.put(ResourceName.stone, 50);
    }
}

@JsonRootName("TrainingCostTable")
class TrainingCostTable {
    @JsonProperty
    Map<UnitName, UnitTrainingCost> trainingCostTable = new EnumMap<>(UnitName.class);

    public TrainingCostTable() {
        trainingCostTable.put(UnitName.farmer, new UnitTrainingCost());
        trainingCostTable.put(UnitName.blacksmith, new UnitTrainingCost());
    }
}

public class App {
    public static void main(String[] args) throws JsonProcessingException {
        SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2020_12,
                OptionPreset.PLAIN_JSON);
        SchemaGeneratorConfig config = configBuilder.build();
        SchemaGenerator generator = new SchemaGenerator(config);
        JsonNode jsonSchema = generator.generateSchema(TrainingCostTable.class);

        System.out.println(jsonSchema.toPrettyString());
        // {
        //   "$schema" : "https://json-schema.org/draft/2020-12/schema",
        //   "type" : "object",
        //   "properties" : {
        //     "trainingCostTable" : {
        //       "type" : "object"
        //     }
        //   }
        // }

        TrainingCostTable foo = new TrainingCostTable();
        ObjectMapper mapper = new ObjectMapper();
        var serialized = mapper.writeValueAsString(foo);
        System.out.println(new JSONObject(serialized).toString(4));

        // {"trainingCostTable": {
        //     "blacksmith": {"trainingCosts": {
        //         "wood": 100,
        //         "food": 150,
        //         "stone": 50
        //     }},
        //     "farmer": {"trainingCosts": {
        //         "wood": 100,
        //         "food": 150,
        //         "stone": 50
        //     }}
        // }}
    }
}

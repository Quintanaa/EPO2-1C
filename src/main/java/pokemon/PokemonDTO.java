package pokemon;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PokemonDTO {
	private String name;
	private int id;
	private int height;
	private int weight;
	private List<String> types;
	private List<String> abilities;

}

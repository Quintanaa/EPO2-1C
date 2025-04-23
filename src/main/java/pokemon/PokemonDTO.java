package pokemon;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PokemonDTO {
	private String name;
	private int id;
	private int height;
	private int weight;
	private List<String> types;
	private List<String> abilities;

}

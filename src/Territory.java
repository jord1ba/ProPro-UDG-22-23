/**
* @file Territory.java
* @author Guillem Vidal
*/
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @brief Classe que engloba totes les regions,
 * simplifica les crides des de simulació.
 */
public class Territory {

	/**
	 * Llista de regions.
	 */
	private final List<Region> _regions;

	/**
	 * @brief Constructor principal.
	 */
	public Territory(List<Region> regions) {

		_regions = regions;

	}

	/**
	 * @brief Cerca de la regió segons el nom.
	 * @return la regió amb el nom name.
	 */
	public Region getRegion(String name) {

		Region region = null;

		for (Region r : _regions) {

			if (r.name().equals(name)) {

				region = r;
				break;

			}

		}

		if (region == null) {

			throw new NoSuchElementException();

		}

		return region;
	}

	/**
	 * @brief Afegeix una vacuna a la regió.
	 */
	public void addVaccine(Vaccine vaccine, float proportion, String region_name) {

		Region region = getRegion(region_name);

		region.addVaccine(vaccine, proportion);

	}

	/**
	 * @brief Propaga i infecta les regions.
	 */
	public void propagateViruses() {

		for (Region region : _regions) region.movements();
		for (Region region : _regions) region.propagate();
		for (Region region : _regions) region.rollbacks();
		for (Region region : _regions) region.infect();

	}

	/**
	 * @return la llista de regions.
	 */
	public List<Region> regionList() {

		return _regions;

	}

}

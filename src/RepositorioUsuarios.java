import java.util.HashSet;
import java.util.Set;

public class RepositorioUsuarios {
	private static RepositorioUsuarios instance = null;
	private Set<String> usuarios;

	private RepositorioUsuarios() {
		this.usuarios = new HashSet<>();
	}

	public static RepositorioUsuarios getInstance() {
		if (instance == null)
			instance = new RepositorioUsuarios();
		return instance;
	}

	public boolean add(String username) {
		return this.usuarios.add(username);
	}

	public void delete(String username) {
		this.usuarios.remove(username);
	}

	public boolean contains(String username) {
		return this.usuarios.contains(username);
	}
}

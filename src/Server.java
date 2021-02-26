import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	private static final int SERVER_PORT = 59898;

	public static void main(String[] args) {
		ArrayList<Conexao> conexoes = new ArrayList<>();
		try {
			ServerSocket server = new ServerSocket(SERVER_PORT);
			Socket socket;
			System.out.println("Porta do Servidor: " + SERVER_PORT);

			while (true) {
				socket = server.accept();

				Conexao con = new Conexao(socket, conexoes);
				Thread thread = new Thread(con);

				conexoes.add(con);
				thread.start();
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}

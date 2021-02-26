import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class Conexao implements Runnable {
	private Socket currentSocket;
	private PrintWriter clientOutput;
	private BufferedReader clientInput;
	private ArrayList<Conexao> conexoes;
	private String username;

	public Conexao(Socket socket, ArrayList<Conexao> conexoes) {
		try {
			this.currentSocket = socket;
			this.clientInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.clientOutput = new PrintWriter(socket.getOutputStream(), true);
			this.conexoes = conexoes;
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	@Override
	public void run() {
		try {
			while (true) {
				if (username == null) {
					cadastrarUsuario();
				} else {
					String inputString = clientInput.readLine().trim();
					if (!inputString.equals("")) {
						if (inputString.toLowerCase().equals("!sair")) {
							RepositorioUsuarios.getInstance().delete(this.username);
							break;
						} else if (inputString.toLowerCase().equals("!listar")) {
							listarUsuarios();
						} else if (inputString.toLowerCase().equals("!pv")) {
							enviarMsgPrivada();
						} else if (inputString.trim().toLowerCase().equals("!comandos")) {
							exibirComandos();
						} else {
							enviarParaTodos(this.username + ": " + inputString);
						}
					}

				}

			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				clientInput.close();
				clientOutput.close();
				currentSocket.close();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	private void cadastrarUsuario() {
		try {
			boolean showWelcome = true;
			if (showWelcome) {
				this.clientOutput.println("Bem vindo, digite o seu nome de usuario.");
			}
			String inputString = clientInput.readLine();
			if (!inputString.trim().equals("")) {
				boolean userExists = RepositorioUsuarios.getInstance().contains(inputString);
				if (userExists) {
					this.clientOutput.println("Erro: O usuário " + inputString + " já existe.");
					this.clientOutput.println("Tente novamente.");
				} else {
					this.username = inputString;
					RepositorioUsuarios.getInstance().add(inputString);
					this.clientOutput.println("Olá " + inputString + ".");
					enviarParaTodos(this.username + " entrou no chat.");
					exibirComandos();
				}
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	private void exibirComandos() {
		this.clientOutput.println("\nComandos do Chat:");
		this.clientOutput.println("!sair - Desconecta do Chat");
		this.clientOutput.println("!listar - Exibe os usuários conectados");
		this.clientOutput.println("!pv - Enviar mensagem para um usuário");
		this.clientOutput.println("!comandos - Exibe os comandos do chat\n");
	}

	private void listarUsuarios() {
		if (RepositorioUsuarios.getInstance().getSize() == 1) {
			this.clientOutput.println("\nNão tem nenhum outro usuário conectado ao servidor.");
		} else {
			this.clientOutput.println("\nLista de Usuários:");
			for (Conexao con : conexoes) {
				if (con.currentSocket != this.currentSocket) {
					this.clientOutput.println(con.username);
				}
			}
		}

	}

	private void enviarMsgPrivada() {
		try {
			boolean mensagemEnviada = false;
			String destinatario = null;
			if (RepositorioUsuarios.getInstance().getSize() == 1) {
				this.clientOutput.println("\nNão tem nenhum outro usuário conectado ao servidor.");
			} else {
				while (!mensagemEnviada) {
					String input = null;
					if (destinatario == null) {
						this.clientOutput.println("\nDigite !voltar para voltar para o chat.");
						this.clientOutput.println("Digite o nome do usuário:");
						input = clientInput.readLine().trim();
						if (!input.equals("")) {
							if (input.toLowerCase().equals("!voltar")) {
								break;
							}
							destinatario = input;
						}
					}
					if (destinatario != null) {
						boolean userExists = RepositorioUsuarios.getInstance().contains(destinatario);
						if (userExists) {
							this.clientOutput.println("\nDigite !voltar para voltar para o menu anterior.");
							this.clientOutput.println("Digite a mensagem:");
							String mensagem = clientInput.readLine().trim();
							if (!mensagem.equals("")) {
								if (mensagem.toLowerCase().equals("!voltar")) {
									destinatario = null;
								} else {
									for (Conexao con : conexoes) {
										if (con.username.equals(destinatario)) {
											con.clientOutput.println("PV de " + this.username + ": " + mensagem);
											mensagemEnviada = true;
										}
									}
								}
							}
						}
						if (!userExists) {
							this.clientOutput.println("\nO usuário " + input + " não está conectado.");
							destinatario = null;
						}
					}
				}
			}

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

	}

	private void enviarParaTodos(String output) {
		for (Conexao con : conexoes) {
			if (con.currentSocket != this.currentSocket) {
				con.clientOutput.println(output);
			}
		}
	}
}

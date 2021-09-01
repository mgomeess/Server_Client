package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Scanner;

public class Client {
	private static Scanner scan;
	public static void main(String[] args) {
		try {
			int opmenu;
			scan = new Scanner(System.in);
			//criacao de conexao por meio de socket
			Socket socket = new Socket("localhost", 5555);
			//criacao de streams de entrada e saída
			ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
			System.out.println("Tipo de Cliente: \n1- Produtor\n2- Consumidor");
			int opcao = scan.nextInt();
			output.write(opcao);
			output.flush();
			
			if (opcao == 1) {//se o cliente for produtor de conteudo
				do {//laco de repeticao para criacao de temas e conteudos
					System.out.println(
							"1- Deseja criar um novo tema?\n2- Deseja inserir conteudo em um tema já existente?\n3- Finalizar");
					opmenu = scan.nextInt();
					output.write(opmenu);
					output.flush();
					
					if (opmenu == 1) {//se o cliente desejar criar um tema
						System.out.println("Informe o nome do tema:\n");
						String tm = scan.next();
						output.writeUTF(tm);
						output.flush();
						String resposta = input.readUTF();
						System.out.println(resposta);

					} else if (opmenu == 2) {//se o cliente deseja criar conteudo para um tema existente
						int n = input.read();
						int a = 0;

						System.out.println("TEMAS EXISTES:");
						while (a < n) {//exibicao de temas existentes
							String t = input.readUTF();//recebendo temas armazenados do servidor
							System.out.println("* " + t);
							a++;
						}
						System.out.println("\n\nInforme o nome do tema que deseja:\n");
						String tm = scan.next();
						output.writeUTF(tm);//envio do tema desejado para o servidor
						output.flush();
						System.out.println("Conteudo:\n");
						scan.nextLine();
						String msg = scan.nextLine();
						output.writeUTF(msg);//envio do conteudo para o servidor no tema desejado
						output.flush();
						msg = input.readUTF();
						System.out.println("SUCESSO!!");
					}
				} while (opmenu == 1 || opmenu == 2);//encerra o loop quando o cliente digita algo !2 ou !1
				System.out.println("operação realizada com sucesso"
						+ "Sistema finalizado");
				input.close();//input encerrado
				socket.close();//output encerrado
			} else if (opcao == 2) { //caso o cliente seja consumidor de conteudo
				int numero = input.read();//recebendo o numero de temas existentes do servidor
				int a = 0;
				System.out.println("TEMAS:");
				while (a < numero) {
					String t = input.readUTF();//lendo os temas que o servidor envia e printando pro cliente
					System.out.println("- " + t);//somente os temas sao printados
					a++;
				}
				System.out.println("\nEscolha um tema de seu interesse: ");
				String tema = scan.next();
				output.writeUTF(tema);//envio do tema desejado para o servidor
				output.flush();
				System.out.println("Tema selecionado: " + tema);//print do tema que o cliente escolheu
				a = 1;
				int k = input.read();//recebendo do servidor o tanto de temas existentes
				while (a <= k) {//k eh o numero de mensagens existentes em um tema
					String mensagens = input.readUTF();//o laco vai printar as mensagens ate chegar no limite -> k
					System.out.println("Conteudo " + a + ": " + mensagens);
					a++;
				}
				System.out.println(input.readUTF());
				input.close();//encerramento do input
				socket.close();//encerramento do output
			}
			input.close();//enc do input
			socket.close();//enc do output
		} catch (IOException ex) {
			System.out.println("Erro no client: " + ex);
			Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
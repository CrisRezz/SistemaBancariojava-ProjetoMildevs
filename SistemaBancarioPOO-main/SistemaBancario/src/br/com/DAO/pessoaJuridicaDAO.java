package br.com.DAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import br.com.ConnectionFactory.ConnectionFactory;
import br.com.POJO.pessoaJuridicaPOJO;


public class pessoaJuridicaDAO {
	
	private Connection conn;
	
	public pessoaJuridicaDAO() {
		this.conn = ConnectionFactory.getConnection();
		
	}

	
	
	public boolean InsereCliente(pessoaJuridicaPOJO clientePJ) {	
		
		String sql = ("INSERT INTO public.cliente_pj(\n"
				+ "	numero_conta, nome_fantasia, cnpj, razao_social, agencia, telefone, saldo, cheque_especial)\n"
				+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?);");
		
		
		PreparedStatement smt;	
		
		try {
				smt = conn.prepareStatement(sql);
				
				smt.setInt(1, clientePJ.getNumeroConta());
				smt.setString(2, clientePJ.getNomeFantasia());
				smt.setString(3,clientePJ.getCnpj());
				smt.setString(4, clientePJ.getRazaoSocial());
				smt.setString(5, clientePJ.getAgencia());
				smt.setString(6, clientePJ.getTelefone());
				smt.setDouble(7, clientePJ.getSaldo() );
				smt.setDouble(8,clientePJ.getChequeEspecial() );
				
				smt.execute();			
				smt.close();
				System.out.println("\nCliente cadastrado!\n");
			return true;
			
		} catch (SQLException e) {
			System.err.println("Não foi possivel cadastrar o cliente" + clientePJ.getNomeFantasia());
			System.err.println(e.getMessage());
			
		}
		
		return false;
	
	}
	
	public pessoaJuridicaPOJO ConsultaCliente(int NumConta ) {
		String sql = "SELECT * FROM cliente_pj WHERE numero_conta = ? ;";
		
		PreparedStatement smt;	
		
		
		try {
			smt = conn.prepareStatement(sql);
			smt.setInt(1, NumConta);
			
			
			ResultSet rs = smt.executeQuery();
			if (rs.getInt("numero_conta") == NumConta) {
				while(rs.next()) {
					
					pessoaJuridicaPOJO clientePJ = new  pessoaJuridicaPOJO();
					clientePJ.setNumeroConta((rs.getInt("numero_conta")));
					clientePJ.setNomeFantasia((rs.getString("nome_fantasia")));
					clientePJ.setCnpj((rs.getString("cnpj")));
					clientePJ.setRazaoSocial((rs.getString("razao_social")));
					clientePJ.setAgencia((rs.getString("agencia")));
					clientePJ.setTelefone((rs.getString("telefone")));
					clientePJ.setSaldo((rs.getDouble("saldo")));
					clientePJ.setChequeEspecial((rs.getDouble("cheque_especial")));
					return clientePJ;
					
				}
			}else {
				
			}
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.err.println("O cliente com  o número de conta: " + NumConta + " não foi encontrado");
			System.err.println(e.getMessage());

		}
	
		return null;
		
	}
	
	public boolean removeCliente(int numeroConta) {

        String sql = "DELETE FROM public.cliente_pj WHERE numero_conta = ?";

        try {
            PreparedStatement stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1, numeroConta);
            ResultSet rs = stmt.executeQuery();
            if(rs.getInt("numero_conta") == numeroConta) {
            	 System.out.println("\nCliente removido!\n");
                 stmt.execute();
                 stmt.close();
            }else {
            	
            }
           
        } catch (SQLException e) {
            System.err.println("Erro ao remover cliente");
            System.err.println(e.getMessage());
            return false;
        }

        return true;
    }
	
	public void TransferenciaClienteParaPJ (int NumcontaDestino, int NumcontaTranferidor, double ValorTransfere, int tipoConta) {
		String view = "SELECT saldo FROM cliente_pj WHERE numero_conta = ? \r\n"
				+ "	\r\n"
				+ "	SELECT saldo FROM cliente_pf WHERE numero_conta = ? ;";

		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(view);
			stmt.setDouble(1, NumcontaDestino);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()) {

				
				
				if (rs.getDouble("saldo") >= ValorTransfere) {
					
					String sql;
					
					  if (tipoConta == 1)  {
					    sql = "UPDATE public.cliente_pj SET  SALDO = (saldo) + ? WHERE numero_conta = ? \r\n;"
								+ "	\n"
								+ "	UPDATE public.cliente_pf SET SALDO = (saldo) - ? WHERE numero_conta = ? ;";
					  } else {
					    sql = "UPDATE public.cliente_pj SET  SALDO = (saldo) + ? WHERE numero_conta = ? \r\n;"
								+ "	\n"
								+ "	UPDATE public.cliente_pj SET SALDO = (saldo) - ? WHERE numero_conta = ? ;";
					  }
					
					
					stmt = conn.prepareStatement(sql);
					stmt.setDouble(1, ValorTransfere);
					stmt.setInt(2, NumcontaDestino);
					stmt.setDouble(3, ValorTransfere);
					stmt.setInt(4, NumcontaTranferidor);

					System.out.println("\nValor tranferido para a conta" + NumcontaDestino + "\n");
					
					 
				}else {
					System.out.println("\nFalha: Valor de transferencia é maior que o saldo atual!");

				}
				
			}
			
			
			stmt.execute();
            stmt.close();
            
           
			
		} catch (SQLException e) {
			System.err.println("Ocorreu um problema em transferir para a conta " + NumcontaDestino);
			System.err.println(e.getMessage());
			
		}
	
		
		
		
	}
	
	public void Depositar (int Numconta, double ValorDeposito) {
		String view = "SELECT * FROM cliente_pj WHERE numero_conta = ? ;";

		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(view);
			stmt.setDouble(1, Numconta);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()) {
				if (ValorDeposito <= 0) {
					
					System.out.println("\nFalha: Valor de transferencia não pode ser 0 ou negativo!");
					
				}else if (rs.getDouble("saldo") >= ValorDeposito) {
					
					String sql = "UPDATE public.cliente_pj\r\n"
							+ "	SET  saldo = (saldo) + ? \r\n"
							+ "	WHERE numero_conta = ? ;";
					
					stmt = conn.prepareStatement(sql);
					stmt.setDouble(1, ValorDeposito);
					stmt.setInt(2, Numconta);
					System.out.println("\nValor depositado!\n");
					
					 
				}else {
					System.out.println("\nFalha: Valor de transferencia é maior que o saldo atual!");

				}
				
			}
			
			
			stmt.execute();
            stmt.close();
            
           
			
		} catch (SQLException e) {
			System.err.println("Ocorreu um problema em transferir para a conta " + Numconta);
			System.err.println(e.getMessage());
			
		}
	}
	
	public void alteraChequeEspecial(double chequeEspecial, int numeroConta) {
        String sql = "UPDATE cliente_pj SET cheque_especial = ? WHERE numero_conta = ?";
        PreparedStatement stmt;

        try {
            stmt = conn.prepareStatement(sql);

            if (chequeEspecial >= 0) {
                stmt.setDouble(1, chequeEspecial);
            } else {
                System.out.println("FALHA: O novo limite deve ser maior ou igual a 0!");
            }
            stmt.setInt(2, numeroConta);
            
            System.out.println("\nCheque alterado com sucesso!\n");
            
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Erro ao alterar o cheque especial da conta " + numeroConta);
            System.err.println(e.getMessage());
        }
    }
	
	public pessoaJuridicaPOJO PercorreLinhaTabela(PreparedStatement smt, List <pessoaJuridicaPOJO> listagemPJ ) {
		
		try {
			ResultSet rs = smt.executeQuery();
			
			while(rs.next()) {
				
			pessoaJuridicaPOJO clientePJ = new  pessoaJuridicaPOJO();
				
				
			clientePJ.setNumeroConta((rs.getInt("numero_conta")));
			clientePJ.setNomeFantasia((rs.getString("nome_fantasia")));
			clientePJ.setCnpj((rs.getString("cnpj")));
			clientePJ.setRazaoSocial((rs.getString("razao_social")));
			clientePJ.setAgencia((rs.getString("agencia")));
			clientePJ.setTelefone((rs.getString("telefone")));
			clientePJ.setSaldo((rs.getDouble("saldo")));
			clientePJ.setChequeEspecial((rs.getDouble("cheque_especial")));
			listagemPJ.add(clientePJ);
			
		}
		} catch (SQLException e) {
			System.err.println("Não foi possivel acessar as informações da tabela");
			System.err.println(e.getMessage());
		}
		
		return null;
		
	}
	
	
	public List<pessoaJuridicaPOJO> ConsultaClientesPJ(){
	
		String sql = "SELECT * FROM public.cliente_pj;";
		
		List <pessoaJuridicaPOJO> consultaPJ = new ArrayList();
		
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			PercorreLinhaTabela(stmt,consultaPJ);
			
			
			
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return consultaPJ;
		
		
		
	}


	public double saldoTotalClientes(){
		
        String sql = "SELECT SUM(Cliente_PF.Saldo) + SUM(Cliente_PJ.Saldo) \r\n"
        		+ "	AS SomaTotal FROM Cliente_PF, Cliente_PJ;";
        

        PreparedStatement stmt;
        try {
        	stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
        	while(rs.next()){
        		 
                 return rs.getDouble("SomaTotal");
        	}
           


            
        } catch (SQLException e) {
            e.printStackTrace();
        }
		return 0;


       

    }
	
}

		
		
		
	
		
	
		
		
		

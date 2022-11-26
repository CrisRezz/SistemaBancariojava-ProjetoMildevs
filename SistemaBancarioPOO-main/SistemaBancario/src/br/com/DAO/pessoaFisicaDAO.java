package br.com.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.ConnectionFactory.ConnectionFactory;
import br.com.POJO.Cliente;
import br.com.POJO.pessoaFisicaPOJO;
import br.com.POJO.pessoaJuridicaPOJO;

public class pessoaFisicaDAO {
	
	public Connection conn;
	
	public pessoaFisicaDAO() {
		super();
		this.conn = ConnectionFactory.getConnection();
		
	}

	
	
	
	public boolean insereCliente(pessoaFisicaPOJO clientePF) {
		String sql = "INSERT INTO public.cliente_pf(numero_conta, nome, cpf, idade, agencia, telefone, saldo, cheque_especial)"
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement stmt;
		try {
			
			 stmt = conn.prepareStatement(sql);
			
			stmt.setInt(1, clientePF.getNumeroConta());
			stmt.setString(2, clientePF.getNome());
			stmt.setString(3, clientePF.getCpf());
			stmt.setInt(4, clientePF.getIdade());
			stmt.setString(5, clientePF.getAgencia());
			stmt.setString(6, clientePF.getTelefone());
			stmt.setDouble(7,clientePF.getSaldo());
			stmt.setDouble(8, clientePF.getChequeEspecial());
			stmt.execute();
			stmt.close();
			
			System.out.println("\nCliente cadastrado!\n");

			return true;
			
		} catch (SQLException e) {
			System.err.println("Erro ao realizar a inserção");
			System.err.println(e.getMessage());
			return false;
		}
	
		
	}
	
	public boolean removeCliente(int numeroConta) {

        String sql = "DELETE FROM public.cliente_pf WHERE numero_conta = ?";

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

	public pessoaFisicaPOJO ConsultaCliente(int NumConta ) {
		String sql = "SELECT * FROM cliente_pf WHERE numero_conta = ? ;";
		
		PreparedStatement smt;	
		
		
		try {
			smt = conn.prepareStatement(sql);
			smt.setInt(1, NumConta);
			
			ResultSet rs = smt.executeQuery();
			if (rs.getInt("numero_conta") == NumConta) {
				while(rs.next()) {
					
					pessoaFisicaPOJO clientePF = new  pessoaFisicaPOJO();
					
					clientePF.setNumeroConta((rs.getInt("numero_conta")));
					clientePF.setNome(rs.getString("nome"));
					clientePF.setCpf(rs.getString("cpf"));
					clientePF.setAgencia((rs.getString("agencia")));
					clientePF.setTelefone((rs.getString("telefone")));
					clientePF.setSaldo((rs.getDouble("saldo")));
					clientePF.setChequeEspecial((rs.getDouble("cheque_especial")));
					return clientePF;
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
	
	public void TransferenciaClienteParaPF (int NumcontaDestino, int NumcontaTranferidor, double ValorTransfere, int tipoConta) {
		String view = "SELECT * FROM cliente_pf WHERE numero_conta = ? ;";

		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(view);
			stmt.setDouble(1, NumcontaDestino);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()) {

				if (rs.getDouble("saldo") >= ValorTransfere) {
					String sql;
					
					  if (tipoConta == 1)  {
					    sql = "UPDATE public.cliente_pf SET  SALDO = (saldo) + ? WHERE numero_conta = ? \r\n;"
								+ "	\n"
								+ "	UPDATE public.cliente_pf SET SALDO = (saldo) - ? WHERE numero_conta = ? ;";
					  } else {
					    sql = "UPDATE public.cliente_pf SET  SALDO = (saldo) + ? WHERE numero_conta = ? \r\n;"
								+ "	\n"
								+ "	UPDATE public.cliente_pj SET SALDO = (saldo) - ? WHERE numero_conta = ? ;";
					  }
					stmt = conn.prepareStatement(sql);
					stmt.setDouble(1, ValorTransfere);
					stmt.setInt(2, NumcontaDestino);
					stmt.setDouble(3, ValorTransfere);
					stmt.setInt(4, NumcontaTranferidor);

					System.out.println("\nValor tranferido!");
					
					 
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
	
	public List<pessoaFisicaPOJO> ConsultaClientesPF(){
		
		String sql = "SELECT * FROM public.cliente_pf;";
		
		List <pessoaFisicaPOJO> consultaPF = new ArrayList();
		
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			PercorreLinhaTabela(stmt,consultaPF);
			
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return consultaPF;
		
	}
	
	public void alteraChequeEspecial(double chequeEspecial, int numeroConta) {
        String sql = "UPDATE cliente_pf SET cheque_especial = ? WHERE numero_conta = ?";
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
            System.err.println("Erro ao remover cliente");
            System.err.println(e.getMessage());
        }
    }
	
	public pessoaFisicaPOJO PercorreLinhaTabela(PreparedStatement smt, List <pessoaFisicaPOJO> listagemPF) {
		
		
		
		try {
			ResultSet rs = smt.executeQuery();
			
			while(rs.next()) {
				
			pessoaFisicaPOJO clientePF = new  pessoaFisicaPOJO();
				
			clientePF.setNumeroConta((rs.getInt("numero_conta")));
			clientePF.setNome(rs.getString("nome"));
			clientePF.setCpf(rs.getString("cpf"));
			clientePF.setAgencia((rs.getString("agencia")));
			clientePF.setTelefone((rs.getString("telefone")));
			clientePF.setSaldo((rs.getDouble("saldo")));
			clientePF.setChequeEspecial((rs.getDouble("cheque_especial")));
			listagemPF.add(clientePF);
			
		}
		} catch (SQLException e) {
			
			System.err.println("Não foi possivel acessar as informações da tabela");
			System.err.println(e.getMessage());
		}
		
		return null;
		
	}
	
	
}
	

	
	


	
	
	


package br.univali.portugol.nucleo.bibliotecas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

// import com.fasterxml.jackson.databind.annotation.JsonAppend.Prop;

// import br.univali.portugol.nucleo.ErroAoRenomearSimbolo.Tipo;
import br.univali.portugol.nucleo.bibliotecas.base.Biblioteca;
import br.univali.portugol.nucleo.bibliotecas.base.ErroExecucaoBiblioteca;
import br.univali.portugol.nucleo.bibliotecas.base.TipoBiblioteca;
import br.univali.portugol.nucleo.bibliotecas.base.anotacoes.Autor;
import br.univali.portugol.nucleo.bibliotecas.base.anotacoes.DocumentacaoBiblioteca;
import br.univali.portugol.nucleo.bibliotecas.base.anotacoes.DocumentacaoFuncao;
import br.univali.portugol.nucleo.bibliotecas.base.anotacoes.DocumentacaoParametro;
// import br.univali.portugol.nucleo.bibliotecas.base.anotacoes.DocumentacaoParametro;
import br.univali.portugol.nucleo.bibliotecas.base.anotacoes.PropriedadesBiblioteca;
import br.univali.portugol.nucleo.programa.Programa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
// import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/**
 * 
 * @author Gabriel
 */


@PropriedadesBiblioteca(tipo = TipoBiblioteca.RESERVADA)
@DocumentacaoBiblioteca(
    descricao = "Esta biblioteca contém diversas funções para subir um servidor",
    versao = "0.1"
)
public final class BancoDeDados extends Biblioteca {

    Programa programa;
    private static final Logger LOGGER = Logger.getLogger(Servidor.class.getName());
    private Connection connection = null;
    private List<String> result = new ArrayList<>();
    private boolean lastError = false;
    private final Lock lock = new ReentrantLock();

    @Override
    public void inicializar(Programa programa, List<Biblioteca> bibliotecasReservadas) throws ErroExecucaoBiblioteca, InterruptedException {
        this.programa = programa;
        super.inicializar(programa, bibliotecasReservadas);
    }

    @DocumentacaoFuncao(
        descricao = "Faz o bind do servidor em uma porta e aguarda conexões",
        retorno = "Mensagem OK",
        parametros = 
        {
            @DocumentacaoParametro(nome = "url_conexao", descricao = "o caminho da rota"),
            @DocumentacaoParametro(nome = "usuario", descricao = "o caminho da rota"),
            @DocumentacaoParametro(nome = "senha", descricao = "o caminho da rota")
        },
        autores = 
        {
            @Autor(nome = "Gabriel Martins de Lima", email = "gabrielmrts@yahoo.com")
        }
    )
    public boolean conectar(String url_conexao, String usuario, String senha) throws ErroExecucaoBiblioteca, InterruptedException {
        String url = url_conexao;
        String user = usuario;
        String password = senha;
        try {
            if (this.connection == null) {
                this.connection = DriverManager.getConnection(url, user, password);
            }
            return true;   
        } catch (SQLException e) {
            throw new ErroExecucaoBiblioteca("Erro ao se conectar ao banco de dados: " + e.getMessage());
        }

    }

    @DocumentacaoFuncao(
        descricao = "Faz o bind do servidor em uma porta e aguarda conexões",
        retorno = "Mensagem OK",
        parametros = 
        {
            @DocumentacaoParametro(nome = "query", descricao = "o caminho da rota")
        },
        autores = 
        {
            @Autor(nome = "Gabriel Martins de Lima", email = "gabrielmrts@yahoo.com")
        }
    )
    public void consulta(String query) throws ErroExecucaoBiblioteca, InterruptedException {
        this.result.clear();

        try {
            try (Statement statement = this.connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {
                    while (resultSet.next()) {
                        this.result.add(resultSet.getString("resultado_json"));
                    }
                }
        } catch (Exception e) {
            throw new ErroExecucaoBiblioteca(e.getMessage());
        }

    }

    @DocumentacaoFuncao(
        descricao = "Faz o bind do servidor em uma porta e aguarda conexões",
        retorno = "Mensagem OK",
        parametros = 
        {
            @DocumentacaoParametro(nome = "query", descricao = "o caminho da rota")
        },
        autores = 
        {
            @Autor(nome = "Gabriel Martins de Lima", email = "gabrielmrts@yahoo.com")
        }
    )
    public void chamar_funcao_sql(String query) throws ErroExecucaoBiblioteca, InterruptedException {
        lock.lock();
        this.result.clear();
        this.lastError = false;

        try {
            try (CallableStatement callableStatement = this.connection.prepareCall(query)) {
                callableStatement.execute();
            } catch (SQLException e) {
                this.lastError = true;
            }
        } catch (Exception e) {
            throw new ErroExecucaoBiblioteca(e.getMessage());
        } finally {
            lock.unlock();
        }

    }

    @DocumentacaoFuncao(
        descricao = "Faz o bind do servidor em uma porta e aguarda conexões",
        retorno = "Mensagem OK",
        // parametros = 
        // {
        //     @DocumentacaoParametro(nome = "query", descricao = "o caminho da rota")
        // },
        autores = 
        {
            @Autor(nome = "Gabriel Martins de Lima", email = "gabrielmrts@yahoo.com")
        }
    )
    public boolean teve_erro() throws ErroExecucaoBiblioteca, InterruptedException {
        try {
            return this.lastError;
        } catch (Exception e) {
            throw new ErroExecucaoBiblioteca(e.getMessage());
        }

    }

    @DocumentacaoFuncao(
        descricao = "Faz o bind do servidor em uma porta e aguarda conexões",
        retorno = "Mensagem OK",
        // parametros = 
        // {
        //     @DocumentacaoParametro(nome = "query", descricao = "o caminho da rota")
        // },
        autores = 
        {
            @Autor(nome = "Gabriel Martins de Lima", email = "gabrielmrts@yahoo.com")
        }
    )
    public boolean ainda_tem_resultado() throws ErroExecucaoBiblioteca, InterruptedException {
        try {
            if (this.result.size() == 0) {
                return false;
            } 
            return true;
        } catch (Exception e) {
            throw new ErroExecucaoBiblioteca(e.getMessage());
        }

    }

    @DocumentacaoFuncao(
        descricao = "Faz o bind do servidor em uma porta e aguarda conexões",
        retorno = "Mensagem OK",
        // parametros = 
        // {
        //     @DocumentacaoParametro(nome = "query", descricao = "o caminho da rota")
        // },
        autores = 
        {
            @Autor(nome = "Gabriel Martins de Lima", email = "gabrielmrts@yahoo.com")
        }
    )
    public String pegar_linha() throws ErroExecucaoBiblioteca, InterruptedException {
        try {
            return this.result.remove(0);
        } catch (Exception e) {
            throw new ErroExecucaoBiblioteca(e.getMessage());
        }

    }


}
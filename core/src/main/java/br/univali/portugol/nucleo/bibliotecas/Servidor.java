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
import br.univali.portugol.nucleo.bibliotecas.base.anotacoes.DocumentacaoConstante;
import br.univali.portugol.nucleo.bibliotecas.base.anotacoes.DocumentacaoFuncao;
import br.univali.portugol.nucleo.bibliotecas.base.anotacoes.DocumentacaoParametro;
// import br.univali.portugol.nucleo.bibliotecas.base.anotacoes.DocumentacaoParametro;
import br.univali.portugol.nucleo.bibliotecas.base.anotacoes.PropriedadesBiblioteca;
import br.univali.portugol.nucleo.programa.Programa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
// import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 
 * @author Gabriel
 */


@PropriedadesBiblioteca(tipo = TipoBiblioteca.RESERVADA)
@DocumentacaoBiblioteca(
    descricao = "Esta biblioteca contém diversas funções para subir um servidor",
    versao = "0.1"
)
public final class Servidor extends Biblioteca {

    Programa programa;
    List<HashMap<String, String>> rotas = new ArrayList<>();
    private final List<List<Object>> pending = new ArrayList<>();
    private ServerSocket serverSocket = null;
    private static final Logger LOGGER = Logger.getLogger(Servidor.class.getName());

    @DocumentacaoConstante(
            descricao = "Constante matemática que representa a relação entre o perímetro de uma circunferência e seu diâmetro, em outras palavras: perimetro/diâmetro",
            referencia = "http://pt.wikipedia.org/wiki/Pi"
    )
    public static final String HTTP_OK = "200 OK";

    @DocumentacaoConstante(
        descricao = "Constante matemática que representa a relação entre o perímetro de uma circunferência e seu diâmetro, em outras palavras: perimetro/diâmetro",
        referencia = "http://pt.wikipedia.org/wiki/Pi"
    )
    public static final String HTTP_CREATED = "204 Created";

    @DocumentacaoConstante(
        descricao = "Constante matemática que representa a relação entre o perímetro de uma circunferência e seu diâmetro, em outras palavras: perimetro/diâmetro",
        referencia = "http://pt.wikipedia.org/wiki/Pi"
    )
    public static final String HTTP_NOTFOUND = "404 Not Found";

    @DocumentacaoConstante(
        descricao = "Constante matemática que representa a relação entre o perímetro de uma circunferência e seu diâmetro, em outras palavras: perimetro/diâmetro",
        referencia = "http://pt.wikipedia.org/wiki/Pi"
    )
    public static final String HTTP_UE = "422 Unprocessable Entity";

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
            @DocumentacaoParametro(nome = "caminho", descricao = "o caminho da rota")
        },
        autores = 
        {
            @Autor(nome = "Gabriel Martins de Lima", email = "gabrielmrts@yahoo.com")
        }
    )
    public void adicionar_rota(String caminho) throws ErroExecucaoBiblioteca, InterruptedException {
        try {
            HashMap<String, String> rota = new HashMap<>();
            rota.put("caminho", caminho);
            rotas.add(rota);
        } catch (Exception e) {
            throw new ErroExecucaoBiblioteca("Err");
        }

    }

    @DocumentacaoFuncao(
        descricao = "Faz o bind do servidor em uma porta e aguarda conexões",
        retorno = "Mensagem OK",
        autores = 
        {
            @Autor(nome = "Gabriel Martins de Lima", email = "gabrielmrts@yahoo.com")
        }
    )
    public String mostrar_rotas() throws ErroExecucaoBiblioteca, InterruptedException {   
        try {
            String rotas = "";

            for (HashMap<String,String> hashMap : this.rotas) {
                rotas += hashMap.get("caminho");
            }
    
            return rotas;
        } catch (Exception e) {
            throw new ErroExecucaoBiblioteca("Err");
        }

    }
    
    @DocumentacaoFuncao(
        descricao = "Faz o bind do servidor em uma porta e aguarda conexões",
        retorno = "Mensagem OK",
        parametros = {
            @DocumentacaoParametro(nome = "porta", descricao = "o caminho da rota")
        },
        autores = 
        {
            @Autor(nome = "Gabriel Martins de Lima", email = "gabrielmrts@yahoo.com")
        }
    )
    public String escutar(String porta) throws ErroExecucaoBiblioteca, InterruptedException
    {   
        
        try {
            if (this.serverSocket == null) {
                this.serverSocket = new ServerSocket(Integer.parseInt(porta));
            }

            String result = new String();

            Socket clientSocket = this.serverSocket.accept();
            List<Object> currList = new ArrayList<>();

            LOGGER.info("TEST");
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String requestLine = in.readLine();
            String[] requestParts = requestLine.split(" ");
            String method = requestParts[0];
            String route = requestParts[1];

            currList.add(clientSocket);
            currList.add(in);

            StringBuilder payload = new StringBuilder();
            while(in.ready()) {
                payload.append((char) in.read());
            }

            this.pending.add(currList);
            
            if (method.equals("POST")) {
                String payloadString = payload.toString();
                String jsonPayload = payloadString.substring(payload.indexOf("{"), payload.indexOf("}") + 1);
    
                result = "METHOD:" + method + ".ROUTE:" + route + ",BODY:" + jsonPayload;
            } else {
                result = "METHOD:" + method + ".ROUTE:" + route + ",:" + "";
            } 
            
            return result;
            
        } catch (IOException e) {
            throw new ErroExecucaoBiblioteca("Error ao tentar vincular o servidor à porta " + porta);
        }
    }

    @DocumentacaoFuncao(
        descricao = "Faz o bind do servidor em uma porta e aguarda conexões",
        retorno = "Mensagem OK",
        parametros = {
            @DocumentacaoParametro(nome = "http_status", descricao = "o caminho da rota"),
            @DocumentacaoParametro(nome = "corpo", descricao = "o caminho da rota")
        },
        autores = 
        {
            @Autor(nome = "Gabriel Martins de Lima", email = "gabrielmrts@yahoo.com")
        }
    )
    public void responder(String http_status, String corpo) throws ErroExecucaoBiblioteca, InterruptedException
    {
        try {
            List<Object> currPending = this.pending.remove(0);
            Socket clientSocket = (Socket) currPending.get(0);
            BufferedReader in = (BufferedReader) currPending.get(1);

            OutputStream out = clientSocket.getOutputStream();
            String response = String.format("HTTP/1.1 %s\r\nContent-Type: application/json\r\n\r\n%s", http_status, corpo);

            out.write(response.getBytes());
            in.close();
            out.close();
            clientSocket.close();
        } catch (Exception e) {
            throw new ErroExecucaoBiblioteca(e.getMessage());
        }
    }

}
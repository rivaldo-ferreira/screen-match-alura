package br.com.alura.screenmatch_spring.principal;

import br.com.alura.screenmatch_spring.model.DadosEpisodio;
import br.com.alura.screenmatch_spring.model.DadosSerie;
import br.com.alura.screenmatch_spring.model.DadosTemporada;
import br.com.alura.screenmatch_spring.service.ConsumoApi;
import br.com.alura.screenmatch_spring.service.ConverteDados;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Principal {
    //https://www.omdbapi.com/?t=lost&apikey=915aa981 *** URL EXAMPLE
    private final String API_KEY = "&apikey=915aa981";
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private Scanner leitura = new Scanner(System.in);
    public void exibeMenu(){

        System.out.println("Digite o nome da série: ");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);

        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dados);



		//DADOS TEMPORADA
		System.out.println("--------------------------------------");
		System.out.println("dados temporada".toUpperCase());
		List<DadosTemporada> temporadas = new ArrayList<>();

		for(int i=1; i<= dados.totalTemporadas(); i++){
			json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") +"&season=" + i + API_KEY);
			DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
			temporadas.add(dadosTemporada);
		}

		//foreach compacto
		temporadas.forEach(System.out::println);

        /*
        for(int i=0; i < dados.totalTemporadas(); i++){
            List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
            for(int j=0; j < episodiosTemporada.size(); j++){
                System.out.println(episodiosTemporada.get(j).titulo());
            }
        }*/

        //foreach encadeado e resumido
        temporadas.forEach(t ->t.episodios().forEach(e-> System.out.println(e.titulo())));
    }
}

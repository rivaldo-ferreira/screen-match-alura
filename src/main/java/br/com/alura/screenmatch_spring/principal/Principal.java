package br.com.alura.screenmatch_spring.principal;

import br.com.alura.screenmatch_spring.model.DadosEpisodio;
import br.com.alura.screenmatch_spring.model.DadosSerie;
import br.com.alura.screenmatch_spring.model.DadosTemporada;
import br.com.alura.screenmatch_spring.model.Episodio;
import br.com.alura.screenmatch_spring.service.ConsumoApi;
import br.com.alura.screenmatch_spring.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

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
        System.out.println("*******************************************************");

        //stream
        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                .flatMap(t ->t.episodios().stream())
                .collect(Collectors.toList());

        System.out.println("\nTop 5 episódios".toUpperCase());
        dadosEpisodios.stream()
                .filter(e->!e.avaliacao().equalsIgnoreCase("N/A"))
                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                .limit(5)
                .forEach(System.out::println);

        //novas informacoes da classe episodio
        List<Episodio> episodios = temporadas.stream()
                .flatMap(t ->t.episodios().stream()
                .map(d-> new Episodio(t.numero(),d)))
                .collect(Collectors.toList());

        episodios.forEach(System.out::println);

        System.out.println("*******************************************************");
        System.out.println("Filtre os episódios por ano: ");
        var ano = leitura.nextInt();
        leitura.nextLine();

        LocalDate dataBusca = LocalDate.of(ano, 1, 1);

        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        episodios.stream()
                .filter(e -> e.getDataLancamento(formatador) != null && e.getDataLancamento(formatador).isAfter(dataBusca))
                .forEach(e-> System.out.println(
                        "Temporada: " + e.getTemporada() +
                        " Episódio: " + e.getTitulo() +
                        " Data de Lançamento: " + e.getDataLancamento(formatador)
                ));

    }
}

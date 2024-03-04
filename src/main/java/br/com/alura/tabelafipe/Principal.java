package br.com.alura.tabelafipe;

import br.com.alura.tabelafipe.model.Dados;
import br.com.alura.tabelafipe.model.Modelos;
import br.com.alura.tabelafipe.model.Veiculo;
import br.com.alura.tabelafipe.service.ConectaAPI;
import br.com.alura.tabelafipe.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private ConverteDados conversor = new ConverteDados();
    private ConectaAPI conectaAPI = new ConectaAPI();
    private Scanner leitura = new Scanner(System.in);
    private String ENDERECO = "https://parallelum.com.br/fipe/api/v1/";

    public void exibeMenu() {

        System.out.println("***** OPÇÕES *****"+
                           "\nCarros"         +
                           "\nMotos"           +
                           "\nCaminhoes"       +
                           "\nDigite a categoria que deseja pesquisar: "

        );

        String escolha = leitura.nextLine();

        if (escolha.equalsIgnoreCase("carros")) {

            ENDERECO += "carros/marcas/";

        } else if (escolha.equalsIgnoreCase("motos")) {

            ENDERECO += "motos/marcas/";

        } else if (escolha.equalsIgnoreCase("caminhoes")){

            ENDERECO += "caminhoes/marcas/";

        }

        var json = conectaAPI.obterDados(ENDERECO);
        var marcas = conversor.obterLista(json, Dados.class);
        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.print("\nDigite o código da marca desejada: ");
        String escolha2 = leitura.nextLine();

        json = conectaAPI.obterDados(ENDERECO + escolha2 + "/modelos");
        var modelos = conversor.obterDados(json, Modelos.class);
        modelos.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.print("\nDigite um trecho do nome do veículo para consulta: ");
        String trechoConsulta = leitura.nextLine();

        List<Dados> modeloFiltrado = modelos.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(trechoConsulta.toLowerCase()))
                .collect(Collectors.toList());

        modeloFiltrado.forEach(System.out::println);


        System.out.print("\nDigite o código do modelo desejado: \n");
        String escolha3 = leitura.nextLine();
        ENDERECO = ENDERECO + escolha2 + "/modelos/" + escolha3 + "/anos/";

        json = conectaAPI.obterDados(ENDERECO);
        var anos = conversor.obterLista(json, Dados.class);
        List<Veiculo> veiculos = new ArrayList<>();

        for (int i = 1; i < anos.size(); i++){
            var enderecoAnos = ENDERECO + anos.get(i).codigo();
            json = conectaAPI.obterDados(enderecoAnos);
            var veiculo = conversor.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }

        veiculos.forEach(System.out::println);
    }
}


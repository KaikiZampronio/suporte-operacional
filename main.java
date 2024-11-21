package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.*;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
class SuporteApplication {
    public static void main(String[] args) {
        SpringApplication.run(SuporteApplication.class, args);
    }
}

@Entity
class Equipamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private boolean ativo = true;

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}

@Entity
class Manutencao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String descricao;

    @Enumerated(EnumType.STRING)
    private TipoManutencao tipoManutencao;

    private LocalDate dataRealizacao;
    private double tempoGasto;

    @ManyToOne
    @JoinColumn(name = "id_equipamento", nullable = false)
    private Equipamento equipamento;

    public enum TipoManutencao {
        CORRETIVA, PREVENTIVA
    }

    public Long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoManutencao getTipoManutencao() {
        return tipoManutencao;
    }

    public void setTipoManutencao(TipoManutencao tipoManutencao) {
        this.tipoManutencao = tipoManutencao;
    }

    public LocalDate getDataRealizacao() {
        return dataRealizacao;
    }

    public void setDataRealizacao(LocalDate dataRealizacao) {
        this.dataRealizacao = dataRealizacao;
    }

    public double getTempoGasto() {
        return tempoGasto;
    }

    public void setTempoGasto(double tempoGasto) {
        this.tempoGasto = tempoGasto;
    }

    public Equipamento getEquipamento() {
        return equipamento;
    }

    public void setEquipamento(Equipamento equipamento) {
        this.equipamento = equipamento;
    }
}

@Repository
interface EquipamentoRepository extends JpaRepository<Equipamento, Long> {}

@Repository
interface ManutencaoRepository extends JpaRepository<Manutencao, Long> {
    long countByDataRealizacao(LocalDate data);

    long countByEquipamentoId(Long equipamentoId);

    List<Manutencao> findByTipoManutencao(Manutencao.TipoManutencao tipo);
}

@Service
class ManutencaoService {
    private final ManutencaoRepository manutencaoRepository;

    public ManutencaoService(ManutencaoRepository manutencaoRepository) {
        this.manutencaoRepository = manutencaoRepository;
    }

    public Manutencao salvar(Manutencao manutencao) {
        return manutencaoRepository.save(manutencao);
    }

    public long contarPorData(LocalDate data) {
        return manutencaoRepository.countByDataRealizacao(data);
    }

    public long contarPorEquipamento(Long equipamentoId) {
        return manutencaoRepository.countByEquipamentoId(equipamentoId);
    }

    public List<Manutencao> buscarPorTipo(Manutencao.TipoManutencao tipo) {
        return manutencaoRepository.findByTipoManutencao(tipo);
    }
}

@RestController
@RequestMapping("/api/manutencoes")
class ManutencaoController {
    private final ManutencaoService manutencaoService;

    public ManutencaoController(ManutencaoService manutencaoService) {
        this.manutencaoService = manutencaoService;
    }

    @PostMapping
    public Manutencao salvar(@RequestBody Manutencao manutencao) {
        return manutencaoService.salvar(manutencao);
    }

    @GetMapping("/contar/data")
    public long contarPorData(@RequestParam String data) {
        return manutencaoService.contarPorData(LocalDate.parse(data));
    }

    @GetMapping("/contar/equipamento/{id}")
    public long contarPorEquipamento(@PathVariable Long id) {
        return manutencaoService.contarPorEquipamento(id);
    }

    @GetMapping("/tipo/{tipo}")
    public List<Manutencao> buscarPorTipo(@PathVariable Manutencao.TipoManutencao tipo) {
        return manutencaoService.buscarPorTipo(tipo);
    }
}

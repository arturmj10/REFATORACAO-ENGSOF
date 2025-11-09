package br.com.appsotecnologia.soemp.service.helpdesk;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import br.com.appsotecnologia.soemp.exception.NegocioException;
import br.com.appsotecnologia.soemp.models.administracao.cadastro.Usuario;
import br.com.appsotecnologia.soemp.models.administracao.cadastro.dto.UsuarioDto;
import br.com.appsotecnologia.soemp.models.administracao.cadastro.dto.UsuarioSimplificadoDto;
import br.com.appsotecnologia.soemp.models.arquivo.dto.ArquivoDto;
import br.com.appsotecnologia.soemp.models.arquivo.enums.ArquivoRegistroEnum;
import br.com.appsotecnologia.soemp.models.helpdesk.HelpdeskChamado;
import br.com.appsotecnologia.soemp.models.helpdesk.HelpdeskTramite;
import br.com.appsotecnologia.soemp.models.helpdesk.dto.HelpdeskTramiteDto;
import br.com.appsotecnologia.soemp.models.helpdesk.enums.StatusChamadoEnum;
import br.com.appsotecnologia.soemp.repository.helpdesk.HelpdeskChamadoRepository;
import br.com.appsotecnologia.soemp.repository.helpdesk.HelpdeskTramiteRepository;
import br.com.appsotecnologia.soemp.service.administracao.cadastro.UsuarioService;
import br.com.appsotecnologia.soemp.service.arquivo.ArquivoService;
import br.com.appsotecnologia.soemp.service.helpdesk.mapper.HelpdeskTramiteMapper;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class HelpdeskTramiteService {

	@Autowired
	private HelpdeskTramiteRepository helpdeskTramiteRepository;
	
	@Autowired
	private HelpdeskChamadoRepository helpdeskChamadoRepository;
	
	@Autowired
	private HelpdeskTramiteMapper helpdeskTramiteMapper;
	
	@Autowired
	private ArquivoService arquivoService;
	
	@Autowired
	private UsuarioService usuarioService;

	public List<HelpdeskTramite> getAll() {
		return helpdeskTramiteRepository.findAll();
	}
	
	public List<HelpdeskTramiteDto> getAllDto() {
		return helpdeskTramiteMapper.toDto(getAll());
	}

	public Page<HelpdeskTramiteDto> pesquisar(Pageable pageable) {

		return helpdeskTramiteRepository.findAll(pageable).map(helpdeskTramiteMapper::toDto);
	}
	
	public Page<HelpdeskTramiteDto> pesquisarPorChamado(Long helpdeskChamadoId, Pageable pageable) {
		
		Page<HelpdeskTramiteDto> helpdeskTramiteDto = helpdeskTramiteRepository.findAllByChamadoId(helpdeskChamadoId , pageable).map(helpdeskTramiteMapper::toDto);
		
		helpdeskTramiteDto.getContent().forEach(tramite -> {
	        List<ArquivoDto> arquivos = arquivoService.getAnexosAtivos(ArquivoRegistroEnum.HELPDESK_TRAMITE, tramite.getId());
	        tramite.setArquivos(arquivos);
	    });
		
		return helpdeskTramiteDto;
	}

	public HelpdeskTramiteDto buscarPorId(Long id) {

		Optional<HelpdeskTramite> optional = helpdeskTramiteRepository.findById(id);
		if (optional.isPresent()) {
			
			HelpdeskTramiteDto helpdeskTramiteDto = helpdeskTramiteMapper.toDto(optional.get());
			helpdeskTramiteDto.setArquivos(arquivoService.getAnexosAtivos(ArquivoRegistroEnum.HELPDESK_TRAMITE, id));
			
			return helpdeskTramiteDto;
		}

		return null;
	}
	
	@Transactional
	public HelpdeskTramiteDto editar(HelpdeskTramiteDto helpdeskTramiteDto, MultipartFile[] files) {

		HelpdeskTramite helpdeskTramite = helpdeskTramiteRepository.save(helpdeskTramiteMapper.toEntity(helpdeskTramiteDto));
		desativarArquivos(helpdeskTramiteDto);
		anexarArquivos(helpdeskTramiteDto.getId(), files);
		return helpdeskTramiteMapper.toDto(helpdeskTramite);
	}

	@Transactional
	public HelpdeskTramiteDto incluir(HelpdeskTramiteDto helpdeskTramiteDto, MultipartFile[] files) {

		if(helpdeskTramiteDto.getChamado().getStatus() == StatusChamadoEnum.FINALIZADO) {
			throw new RuntimeException("O chamado já foi finalizado");
		}
		
		Usuario usuarioLogado = usuarioService.buscarLogado();
		Long idUsuario = usuarioLogado.getId();
		
		if(helpdeskTramiteDto.getChamado().getUsuarioAtendente() != null &&
				!helpdeskTramiteDto.getChamado().getUsuarioAtendente().getId().equals(idUsuario)) {
			throw new RuntimeException("Somente o Usuário Atendente pode registrar Trâmites");
		}
			
		helpdeskTramiteDto.setUsuario(UsuarioSimplificadoDto.builder().id(idUsuario).build());
		
		HelpdeskTramite helpdeskTramite = helpdeskTramiteRepository.save(helpdeskTramiteMapper.toEntity(helpdeskTramiteDto));
		desativarArquivos(helpdeskTramiteDto);
		anexarArquivos(helpdeskTramite.getId(), files);
		
		Optional<HelpdeskChamado> optional = helpdeskChamadoRepository.findById(helpdeskTramite.getChamado().getId());
		if(optional.isPresent()) {
			HelpdeskChamado helpdeskChamado = optional.get();
			helpdeskChamado.setStatus(StatusChamadoEnum.AGUARDANDO_CLIENTE.getCodigo());
		}
		return helpdeskTramiteMapper.toDto(helpdeskTramite);
	}

	public void excluir(Long id) {

		Optional<HelpdeskTramite> optional = helpdeskTramiteRepository.findById(id);
		if (optional.isPresent()) {
			HelpdeskTramite helpdeskTramite = optional.get();
			excluir(helpdeskTramite);
		}
	}
	
	public void excluir(HelpdeskTramite helpdeskTramite) {

		helpdeskTramiteRepository.delete(helpdeskTramite);
	}

	public List<HelpdeskTramiteDto> getByHelpdeskChamadoId(Long idHelpdeskChamado) {
		List<HelpdeskTramiteDto> tramites = helpdeskTramiteMapper.toDto(
				helpdeskTramiteRepository.getAllByChamadoId(idHelpdeskChamado));
		
		for(HelpdeskTramiteDto tramite : tramites) {
			tramite.setArquivos(arquivoService.getAnexosAtivos(ArquivoRegistroEnum.HELPDESK_TRAMITE, tramite.getId()));
		}

		return tramites;
	}
	
	@Transactional
	public HelpdeskTramiteDto gerar(HelpdeskTramiteDto helpdeskTramiteDto, MultipartFile[] files) {
		UsuarioDto usuarioLogado = usuarioService.buscarLogadoDto();
		Long idUsuario = usuarioLogado.getId();
		
		helpdeskTramiteDto.setUsuario(UsuarioSimplificadoDto.builder().id(idUsuario).build());
		
		HelpdeskTramite helpdeskTramite = helpdeskTramiteRepository.save(helpdeskTramiteMapper.toEntity(helpdeskTramiteDto));
		desativarArquivos(helpdeskTramiteDto);
		anexarArquivos(helpdeskTramite.getId(), files);
		
		Optional<HelpdeskChamado> optional = helpdeskChamadoRepository.findById(helpdeskTramite.getChamado().getId());
		if(optional.isPresent()) {
			if(usuarioLogado.getPessoa() != null) {
				HelpdeskChamado helpdeskChamado = optional.get();
				helpdeskChamado.setStatus(StatusChamadoEnum.AGUARDANDO_ATENDENTE.getCodigo());
			} else {
				HelpdeskChamado helpdeskChamado = optional.get();
				helpdeskChamado.setStatus(StatusChamadoEnum.AGUARDANDO_CLIENTE.getCodigo());
			}
		}
		
		return helpdeskTramiteMapper.toDto(helpdeskTramite);
	}
	
	private void anexarArquivos(Long id, MultipartFile... files) {

		if (files != null && files.length > 0) {

			for (MultipartFile file : files) {

				String nameFile = StringUtils.cleanPath(file.getOriginalFilename());

				try {
					arquivoService.adicionarAnexo(ArquivoRegistroEnum.HELPDESK_TRAMITE, id, nameFile, file.getContentType(),
							file.getInputStream());
				} catch (IOException e) {
					throw new NegocioException("Erro ao anexar os arquivos", e);
				}
			}

		}
	}

	private void desativarArquivos(HelpdeskTramiteDto helpdeskTramiteDto) {
		for (ArquivoDto arquivoDto : helpdeskTramiteDto.getArquivos()) {
			if (!arquivoDto.isAtivo()) {
				arquivoService.desativarArquivo(arquivoDto);
			}
		}
	}
}

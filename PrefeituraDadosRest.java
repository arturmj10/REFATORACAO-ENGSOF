package br.com.appsotecnologia.rest.pessoa.prefeitura;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URLDecoder;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.google.common.io.Files;

import br.com.appsotecnologia.anexo.EnAnexo;
import br.com.appsotecnologia.bean.login.EnNivelPermissao;
import br.com.appsotecnologia.bean.login.LoginBean;
import br.com.appsotecnologia.bean.login.Seguro;
import br.com.appsotecnologia.bean.pessoa.prefeitura.PrefeituraParametros;
import br.com.appsotecnologia.db.ConnectionDB;
import br.com.appsotecnologia.firebase.Firebase;
import br.com.appsotecnologia.image.Imagem;
import br.com.appsotecnologia.log.AppsoLog;
import br.com.appsotecnologia.rest.AppsoRest;
import br.com.appsotecnologia.rest.agendamento.AgendamentoStatusRest;
import br.com.appsotecnologia.rest.denuncia.DenunciaStatusRest;
import br.com.appsotecnologia.rest.processo.ProcessoStatusRest;
import br.com.appsotecnologia.rest.solicitacao.SolicitacaoStatusRest;

@Seguro({ EnNivelPermissao.ADMINISTRADOR })
@Path("/prefeituradados")
public class PrefeituraDadosRest extends AppsoRest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1151129410379443925L;

	private static final String RELATORIO_DOCUMENTO = "documento";
	private static final String RELATORIO_CLUBE_MAES = "clubemaes";
	private static final String RELATORIO_DENUNCIA = "denuncia";
	private static final String RELATORIO_SOLICITACAO = "solicitacao";
	private static final String RELATORIO_AGENDAMENTO = "agendamento";

	@POST
	@Path("/logoapp")
	@Seguro({ EnNivelPermissao.PREFEITURA_ADMINISTRADOR })
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addLogo(@Context SecurityContext securityContext, @FormDataParam("logo") String logoParam, /**/
			@FormDataParam("file") InputStream fileInputStream, @FormDataParam("file") FormDataContentDisposition contentDispositionHeader /**/
	) throws Exception {

		LoginBean loginBean = getLoginBean(securityContext);
		if (fileInputStream != null) {
			String linkRetorno = Firebase.enviarArquivo(loginBean.getIdPessoa(), EnAnexo.LOGOAPP, "logo.png", Imagem.convert(fileInputStream, 1280, true));
			return Response.ok("{\"erro\":false, \"link\":\"" + linkRetorno + "\"}").build();
		}

		return Response.ok("{\"erro\":true}").build();
	}

	@DELETE
	@Path("/logoapp")
	@Seguro({ EnNivelPermissao.PREFEITURA_ADMINISTRADOR })
	@Produces(MediaType.APPLICATION_JSON)
	public Response delLogo(@Context SecurityContext securityContext) throws Exception {

		LoginBean loginBean = getLoginBean(securityContext);
		Firebase.excluirArquivo(loginBean.getIdPessoa(), EnAnexo.LOGOAPP, "logo.png");
		return Response.ok("{\"erro\":false}").build();
	}

	@GET
	@Produces({ MediaType.TEXT_PLAIN })
	@Seguro({ EnNivelPermissao.ADMINISTRADOR, EnNivelPermissao.PREFEITURA_ADMINISTRADOR, EnNivelPermissao.USUARIO_FINAL })
	@Path("/logoapp")
	public Response getLogoApp(@Context SecurityContext securityContext, @QueryParam("id_pessoa") Integer id_pessoa) {

		try {

			if (id_pessoa == null) {
				id_pessoa = getLoginBean(securityContext).getIdPessoa();
			}

			String linkRetorno = Firebase.getUrlArquivo(id_pessoa, EnAnexo.LOGOAPP, "logo.png");
			return Response.ok("{\"erro\":false, \"link\":\"" + linkRetorno + "\"}").build();
		} catch (NoSuchAlgorithmException e) {
			AppsoLog.getLog().error("", e);
			return Response.ok("{\"erro\":true}").build();
		}
	}

	@POST
	@Path("/logoreport")
	@Seguro({ EnNivelPermissao.PREFEITURA_ADMINISTRADOR })
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addLogoReport(@Context SecurityContext securityContext, @FormDataParam("logo") String logoParam, /**/
			@FormDataParam("file") InputStream fileInputStream, @FormDataParam("file") FormDataContentDisposition contentDispositionHeader /**/
	) throws Exception {

		LoginBean loginBean = getLoginBean(securityContext);
		if (fileInputStream != null) {
			String linkRetorno = Firebase.enviarArquivo(loginBean.getIdPessoa(), EnAnexo.LOGOREPORT, "logoreport.png", Imagem.convert(fileInputStream, 1280, true));
			return Response.ok("{\"erro\":false, \"link\":\"" + linkRetorno + "\"}").build();
		}

		return Response.ok("{\"erro\":true}").build();
	}

	@DELETE
	@Path("/logoreport")
	@Seguro({ EnNivelPermissao.PREFEITURA_ADMINISTRADOR })
	@Produces(MediaType.APPLICATION_JSON)
	public Response delLogoReport(@Context SecurityContext securityContext) throws Exception {

		LoginBean loginBean = getLoginBean(securityContext);
		Firebase.excluirArquivo(loginBean.getIdPessoa(), EnAnexo.LOGOREPORT, "logoreport.png");
		return Response.ok("{\"erro\":false}").build();
	}

	@GET
	@Produces({ MediaType.TEXT_PLAIN })
	@Seguro({ EnNivelPermissao.ADMINISTRADOR, EnNivelPermissao.PREFEITURA_ADMINISTRADOR, EnNivelPermissao.USUARIO_FINAL })
	@Path("/logoreport")
	public Response getLogoReport(@Context SecurityContext securityContext, @QueryParam("id_pessoa") Integer id_pessoa) {

		try {

			if (id_pessoa == null) {
				id_pessoa = getLoginBean(securityContext).getIdPessoa();
			}

			String linkRetorno = Firebase.getUrlArquivo(id_pessoa, EnAnexo.LOGOREPORT, "logoreport.png");
			return Response.ok("{\"erro\":false, \"link\":\"" + linkRetorno + "\"}").build();
		} catch (NoSuchAlgorithmException e) {
			AppsoLog.getLog().error("", e);
			return Response.ok("{\"erro\":true}").build();
		}
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Seguro({ EnNivelPermissao.ADMINISTRADOR, EnNivelPermissao.PREFEITURA_ADMINISTRADOR })
	@Path("/listastatusagendamento")
	public Response getStatusAgendamento(@Context SecurityContext securityContext) throws Exception {

		LoginBean loginBean = getLoginBean(securityContext);
		return Response.ok(new AgendamentoStatusRest().getListAllCidade(loginBean.getIdPessoa())).build();
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Seguro({ EnNivelPermissao.ADMINISTRADOR, EnNivelPermissao.PREFEITURA_ADMINISTRADOR })
	@Path("/listastatusdenuncia")
	public Response getStatusDenuncia(@Context SecurityContext securityContext) throws Exception {

		LoginBean loginBean = getLoginBean(securityContext);
		return Response.ok(new DenunciaStatusRest().getListAllCidade(loginBean.getIdPessoa())).build();
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Seguro({ EnNivelPermissao.ADMINISTRADOR, EnNivelPermissao.PREFEITURA_ADMINISTRADOR })
	@Path("/listastatussolicitacao")
	public Response getStatusSolicitacao(@Context SecurityContext securityContext) throws Exception {

		LoginBean loginBean = getLoginBean(securityContext);
		return Response.ok(new SolicitacaoStatusRest().getListAllCidade(loginBean.getIdPessoa())).build();
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Seguro({ EnNivelPermissao.ADMINISTRADOR, EnNivelPermissao.PREFEITURA_ADMINISTRADOR })
	@Path("/listastatusprocesso")
	public Response getStatusProcesso(@Context SecurityContext securityContext) throws Exception {

		LoginBean loginBean = getLoginBean(securityContext);
		return Response.ok(new ProcessoStatusRest().getListAllCidade(loginBean.getIdPessoa())).build();
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Seguro({ EnNivelPermissao.ADMINISTRADOR, EnNivelPermissao.PREFEITURA_ADMINISTRADOR, EnNivelPermissao.USUARIO_FINAL })
	@Path("/parametros")
	public Response getParametros(@Context SecurityContext securityContext) throws Exception {

		LoginBean loginBean = getLoginBean(securityContext);
		PrefeituraParametros parametros = new PrefeituraParametros();

		ConnectionDB connectionDB = null;
		PreparedStatement st = null;
		try {
			connectionDB = new ConnectionDB(null);

			st = connectionDB.getConn().prepareStatement("select fl_consulta_processo, fl_rever_cred_qrcode, fl_permitir_login_anonimo, fl_ranking_peso, nr_ranking_dias_peso, fl_ranking_participacao, nr_ranking_dias_participacao, fl_revercred_importacao_cartao, dt_ranking_dias_peso, dt_ranking_dias_participacao, fl_reciclagem_iptu, vl_limite_credito_cidadao, fl_contato_nome_obrigatorio, fl_contato_documento_obrigatorio, fl_contato_email_obrigatorio, nr_dias_arquivar_agendamento, nr_dias_arquivar_denuncia, nr_dias_arquivar_solicitacao, nr_dias_arquivar_processo, nr_raio_metros_maps_contatos, fl_vale_feira_integral, dt_inicio_vale_feira_integral, fl_cartao_revercred, fl_mostrar_opcao_debito_revercred, fl_processo_libera_tramite, fl_revercred_uniforme, fl_contato_telefone_obrigatorio, vl_limpeza_lote_metro_quadrado, fl_limpeza_lote_credenciamento_app from pessoa where id_pessoa = ?");
			st.setInt(1, loginBean.getIdPessoa());
			ResultSet rs = st.executeQuery();
			if (rs.next()) {

				Timestamp dt_ranking_dias_peso_timestamp = rs.getTimestamp("dt_ranking_dias_peso");
				Date dt_ranking_dias_peso = null;
				if (dt_ranking_dias_peso_timestamp != null) {
					dt_ranking_dias_peso = new Date(dt_ranking_dias_peso_timestamp.getTime());
				}

				Timestamp dt_ranking_dias_participacao_timestamp = rs.getTimestamp("dt_ranking_dias_participacao");
				Date dt_ranking_dias_participacao = null;
				if (dt_ranking_dias_participacao_timestamp != null) {
					dt_ranking_dias_participacao = new Date(dt_ranking_dias_participacao_timestamp.getTime());
				}

				Timestamp dt_inicio_vale_feira_integral_timestamp = rs.getTimestamp("dt_inicio_vale_feira_integral");
				Date dt_inicio_vale_feira_integral = null;
				if (dt_inicio_vale_feira_integral_timestamp != null) {
					dt_inicio_vale_feira_integral = new Date(dt_inicio_vale_feira_integral_timestamp.getTime());
				}

				parametros.setProcessoCidadao(rs.getBoolean("fl_consulta_processo"));
				parametros.setPagamentoQrCode(rs.getBoolean("fl_rever_cred_qrcode"));
				parametros.setLoginAnonimo(rs.getBoolean("fl_permitir_login_anonimo"));
				parametros.setRankingPeso(rs.getBoolean("fl_ranking_peso"));
				parametros.setRankingDiasPeso(rs.getInt("nr_ranking_dias_peso"));
				parametros.setRankingParticipacao(rs.getBoolean("fl_ranking_participacao"));
				parametros.setRankingDiasParticipacao(rs.getInt("nr_ranking_dias_participacao"));
				parametros.setImportacaoCartao(rs.getBoolean("fl_revercred_importacao_cartao"));
				parametros.setReciclagemIptu(rs.getBoolean("fl_reciclagem_iptu"));
				parametros.setContatoNomeObrigatorio(rs.getBoolean("fl_contato_nome_obrigatorio"));
				parametros.setContatoDocumentoObrigatorio(rs.getBoolean("fl_contato_documento_obrigatorio"));
				parametros.setContatoTelefoneObrigatorio(rs.getBoolean("fl_contato_telefone_obrigatorio"));
				parametros.setContatoEmailObrigatorio(rs.getBoolean("fl_contato_email_obrigatorio"));
				parametros.setRankingDataPeso(dt_ranking_dias_peso);
				parametros.setRankingDataParticipacao(dt_ranking_dias_participacao);
				parametros.setLimiteCreditoCidadao(rs.getDouble("vl_limite_credito_cidadao"));
				parametros.setDiasArquivarAgendamento(rs.getInt("nr_dias_arquivar_agendamento"));
				parametros.setDiasArquivarDenuncia(rs.getInt("nr_dias_arquivar_denuncia"));
				parametros.setDiasArquivarSolicitacao(rs.getInt("nr_dias_arquivar_solicitacao"));
				parametros.setDiasArquivarProcesso(rs.getInt("nr_dias_arquivar_processo"));
				parametros.setRaioMetrosMapsContatos(rs.getInt("nr_raio_metros_maps_contatos"));
				parametros.setValeFeiraIntegral(rs.getBoolean("fl_vale_feira_integral"));
				parametros.setDataValeFeiraIntegral(dt_inicio_vale_feira_integral);
				parametros.setCartaoRevercred(rs.getBoolean("fl_cartao_revercred"));
				parametros.setMostrarOpcaoDebitoRevercred(rs.getBoolean("fl_mostrar_opcao_debito_revercred"));
				parametros.setLiberaRegistroTramiteProcesso(rs.getBoolean("fl_processo_libera_tramite"));
				parametros.setRevercredUniforme(rs.getBoolean("fl_revercred_uniforme"));
				parametros.setValorLimpezaLoteMetroQuadrado(rs.getDouble("vl_limpeza_lote_metro_quadrado"));
				parametros.setLimpezaLoteCredenciamentoApp(rs.getBoolean("fl_limpeza_lote_credenciamento_app"));

			}
		} finally {
			if (st != null) {
				st.close();
			}
			if (connectionDB != null) {
				connectionDB.close();
			}
		}

		return Response.ok(parametros).build();

	}

	@PUT
	@Path("/parametros")
	@Seguro({ EnNivelPermissao.PREFEITURA_ADMINISTRADOR })
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateParametros(@Context SecurityContext securityContext, PrefeituraParametros parametros) throws Exception {

		LoginBean loginBean = getLoginBean(securityContext);

		ConnectionDB connectionDB = null;
		PreparedStatement st = null;
		try {
			connectionDB = new ConnectionDB(null);

			st = connectionDB.getConn().prepareStatement("update pessoa set fl_consulta_processo = ?, fl_rever_cred_qrcode = ?, fl_permitir_login_anonimo = ?, fl_ranking_peso = ?, nr_ranking_dias_peso = ?, fl_ranking_participacao = ?, nr_ranking_dias_participacao = ?, fl_revercred_importacao_cartao = ?, dt_ranking_dias_peso = ?, dt_ranking_dias_participacao = ?, fl_reciclagem_iptu = ?, vl_limite_credito_cidadao=?, fl_contato_nome_obrigatorio = ?, fl_contato_documento_obrigatorio = ?, fl_contato_email_obrigatorio = ?, nr_dias_arquivar_agendamento = ?, nr_dias_arquivar_denuncia = ?, nr_dias_arquivar_solicitacao = ?, nr_dias_arquivar_processo = ?, nr_raio_metros_maps_contatos = ?, fl_vale_feira_integral = ?, dt_inicio_vale_feira_integral = ?, fl_cartao_revercred = ?, fl_mostrar_opcao_debito_revercred = ?, fl_processo_libera_tramite = ?, fl_revercred_uniforme = ?, fl_contato_telefone_obrigatorio = ?, vl_limpeza_lote_metro_quadrado = ?, fl_limpeza_lote_credenciamento_app = ? where id_pessoa = ?");

			int i = 0;
			st.setBoolean(++i, parametros.isProcessoCidadao());
			st.setBoolean(++i, parametros.isPagamentoQrCode());
			st.setBoolean(++i, parametros.isLoginAnonimo());

			st.setBoolean(++i, parametros.isRankingPeso());
			if (parametros.getRankingDiasPeso() != null && parametros.getRankingDiasPeso() > 0) {
				st.setInt(++i, parametros.getRankingDiasPeso());
			} else {
				st.setNull(++i, java.sql.Types.INTEGER);
			}

			st.setBoolean(++i, parametros.isRankingParticipacao());
			if (parametros.getRankingDiasParticipacao() != null && parametros.getRankingDiasParticipacao() > 0) {
				st.setInt(++i, parametros.getRankingDiasParticipacao());
			} else {
				st.setNull(++i, java.sql.Types.INTEGER);
			}

			st.setBoolean(++i, parametros.isImportacaoCartao());

			if (parametros.getRankingDataPeso() != null) {
				st.setTimestamp(++i, new Timestamp(parametros.getRankingDataPeso().getTime()));
			} else {
				st.setNull(++i, java.sql.Types.TIMESTAMP);
			}

			if (parametros.getRankingDataParticipacao() != null) {
				st.setTimestamp(++i, new Timestamp(parametros.getRankingDataParticipacao().getTime()));
			} else {
				st.setNull(++i, java.sql.Types.TIMESTAMP);
			}

			st.setBoolean(++i, parametros.isReciclagemIptu());
			if (parametros.getLimiteCreditoCidadao() != null) {
				st.setDouble(++i, parametros.getLimiteCreditoCidadao());
			} else {
				st.setNull(++i, java.sql.Types.DOUBLE);
			}

			st.setBoolean(++i, parametros.isContatoNomeObrigatorio());
			st.setBoolean(++i, parametros.isContatoDocumentoObrigatorio());
			st.setBoolean(++i, parametros.isContatoEmailObrigatorio());

			if (parametros.getDiasArquivarAgendamento() != null && parametros.getDiasArquivarAgendamento() > 0) {
				st.setInt(++i, parametros.getDiasArquivarAgendamento());
			} else {
				st.setNull(++i, java.sql.Types.INTEGER);
			}

			if (parametros.getDiasArquivarDenuncia() != null && parametros.getDiasArquivarDenuncia() > 0) {
				st.setInt(++i, parametros.getDiasArquivarDenuncia());
			} else {
				st.setNull(++i, java.sql.Types.INTEGER);
			}

			if (parametros.getDiasArquivarSolicitacao() != null && parametros.getDiasArquivarSolicitacao() > 0) {
				st.setInt(++i, parametros.getDiasArquivarSolicitacao());
			} else {
				st.setNull(++i, java.sql.Types.INTEGER);
			}

			if (parametros.getDiasArquivarProcesso() != null && parametros.getDiasArquivarProcesso() > 0) {
				st.setInt(++i, parametros.getDiasArquivarProcesso());
			} else {
				st.setNull(++i, java.sql.Types.INTEGER);
			}

			st.setInt(++i, parametros.getRaioMetrosMapsContatos());
			st.setBoolean(++i, parametros.isValeFeiraIntegral());

			if (parametros.getDataValeFeiraIntegral() != null) {
				st.setTimestamp(++i, new Timestamp(parametros.getDataValeFeiraIntegral().getTime()));
			} else {
				st.setNull(++i, java.sql.Types.TIMESTAMP);
			}

			st.setBoolean(++i, parametros.isCartaoRevercred());
			st.setBoolean(++i, parametros.isMostrarOpcaoDebitoRevercred());
			st.setBoolean(++i, parametros.isLiberaRegistroTramiteProcesso());
			st.setBoolean(++i, parametros.isRevercredUniforme());
			st.setBoolean(++i, parametros.isContatoTelefoneObrigatorio());

			if (parametros.getValorLimpezaLoteMetroQuadrado() != null) {
				st.setDouble(++i, parametros.getValorLimpezaLoteMetroQuadrado());
			} else {
				st.setNull(++i, java.sql.Types.DOUBLE);
			}

			st.setBoolean(++i, parametros.isLimpezaLoteCredenciamentoApp());

			st.setInt(++i, loginBean.getIdPessoa());

			int executeUpdate = st.executeUpdate();

			return Response.ok(executeUpdate).build();
		} finally {
			if (st != null) {
				st.close();
			}
			if (connectionDB != null) {
				connectionDB.close();
			}
		}
	}
	
	@POST
    @Path("/{tipo}")
    @Seguro({ EnNivelPermissao.PREFEITURA_ADMINISTRADOR })
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response salvarRelatorio(@Context SecurityContext securityContext,
                                    @PathParam("tipo") String tipo,
                                    FormDataMultiPart data) throws Exception {

        String tipoRelatorio = getTipoRelatorio(tipo);
        return salvarRelatorio(securityContext, data, tipoRelatorio);
    }

    @GET
    @Path("/{tipo}")
    @Seguro({ EnNivelPermissao.PREFEITURA_ADMINISTRADOR })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRelatorio(@Context SecurityContext securityContext,
                                 @PathParam("tipo") String tipo) throws Exception {

        String tipoRelatorio = getTipoRelatorio(tipo);
        return getArquivo(securityContext, tipoRelatorio);
    }

    @GET
    @Path("/download/{tipo}")
    @Seguro({ EnNivelPermissao.PREFEITURA_ADMINISTRADOR })
    @Produces(MediaType.APPLICATION_JSON)
    public Response downloadRelatorio(@Context SecurityContext securityContext,
                                      @PathParam("tipo") String tipo) throws Exception {

        String tipoRelatorio = getTipoRelatorio(tipo);
        return downloadArquivo(securityContext, tipoRelatorio);
    }
    
    private String getTipoRelatorio(String tipo) {
        switch (tipo.toLowerCase()) {
            case "documentosxlsx":
            case "documento":
                return RELATORIO_DOCUMENTO;

            case "clubemaes":
            case "relatorioclubemaes":
                return RELATORIO_CLUBE_MAES;
            	
            case "denuncia":
            	return RELATORIO_DENUNCIA;
            	
            case "solicitacao":
            	return RELATORIO_SOLICITACAO;
            	
            case "agendamento":
            	return RELATORIO_AGENDAMENTO;
            default:
                throw new WebApplicationException("Tipo de relatório inválido: " + tipo, Response.Status.BAD_REQUEST);
        }
    }
	
	private Response salvarRelatorio(SecurityContext securityContext, FormDataMultiPart data, String relatorio) throws Exception {
		LoginBean loginBean = getLoginBean(securityContext);

		FormDataBodyPart formDataBodyPart = data.getField("filerelatorio");
		if (formDataBodyPart != null) {

			String nomeArquivo = URLDecoder.decode(formDataBodyPart.getContentDisposition().getFileName(), "UTF-8");
			if (nomeArquivo.lastIndexOf(".jrxml") > -1) {

				ConnectionDB connectionDB = null;
				PreparedStatement stPessoa = null;
				try {

					connectionDB = new ConnectionDB(null);

					stPessoa = connectionDB.getConn().prepareStatement("select ds_caminho_relatorio from pessoa where id_pessoa = 1");
					ResultSet rsPessoa = stPessoa.executeQuery();
					if (rsPessoa.next()) {
						String caminho = rsPessoa.getString("ds_caminho_relatorio");

						if (relatorio == RELATORIO_DOCUMENTO) {
							caminho += "/" + relatorio + "/" + loginBean.getIdPessoa() + "/xlsx/";
						} else {
							caminho += "/" + relatorio + "/" + loginBean.getIdPessoa() + "/";
						}

						File fileCaminho = new File(caminho);
						if (!fileCaminho.exists()) {
							Files.createParentDirs(fileCaminho);
						}

						String arquivoCompilado = nomeArquivo.replace(".jrxml", ".jasper");
						new File(caminho + arquivoCompilado).delete();

						File[] filesOld = fileCaminho.listFiles();
						if (filesOld != null) {

							for (int i = 0; i < filesOld.length; i++) {

								if (filesOld[i].isFile()) {
									FileUtils.moveFileToDirectory(filesOld[i], new File(caminho + "/" + new SimpleDateFormat("yyyyMMddHHmm").format(new java.util.Date())), true);
								}

							}

						}

						File fileRelatorio = new File(caminho + nomeArquivo);
						FileUtils.copyInputStreamToFile(formDataBodyPart.getValueAs(InputStream.class), fileRelatorio);

						return Response.ok("{\"erro\":false}").build();

					}

				} finally {
					if (stPessoa != null) {
						stPessoa.close();
					}
					if (connectionDB != null) {
						connectionDB.close();
					}
				}

			} else {

				// TODO arquivo invalido

			}
		}

		return Response.ok("{\"erro\":true}").build();
	}
	
	private Response downloadArquivo(SecurityContext securityContext, String relatorio) throws Exception {
		LoginBean loginBean = getLoginBean(securityContext);

		ConnectionDB connectionDB = null;
		PreparedStatement stPessoa = null;
		try {

			connectionDB = new ConnectionDB(null);

			stPessoa = connectionDB.getConn().prepareStatement("select ds_caminho_relatorio from pessoa where id_pessoa = 1");
			ResultSet rsPessoa = stPessoa.executeQuery();
			if (rsPessoa.next()) {
				String caminho = rsPessoa.getString("ds_caminho_relatorio");
				
				if (relatorio == RELATORIO_DOCUMENTO) {
					caminho += "/" + relatorio + "/" + loginBean.getIdPessoa() + "/xlsx/";
				} else {
					caminho += "/" + relatorio + "/" + loginBean.getIdPessoa() + "/";
				}

				File fileCaminho = new File(caminho);
				if (fileCaminho.exists()) {

					File[] files = fileCaminho.listFiles(new FilenameFilter() {

						@Override
						public boolean accept(File dir, String name) {
							return name.indexOf(".jrxml") > -1;
						}
					});

					if (files != null && files.length > 0) {

						return Response.ok(FileUtils.readFileToByteArray(files[0])).type("application/jrxml").header("Content-Disposition", "filename=\"" + files[0].getName() + "\"").build();

					}

				}

			}

		} finally {
			if (stPessoa != null) {
				stPessoa.close();
			}
			if (connectionDB != null) {
				connectionDB.close();
			}
		}

		return Response.serverError().build();
	}
	
	private Response getArquivo(SecurityContext securityContext, String relatorio) throws Exception{
		LoginBean loginBean = getLoginBean(securityContext);

		ConnectionDB connectionDB = null;
		PreparedStatement stPessoa = null;
		try {

			connectionDB = new ConnectionDB(null);

			stPessoa = connectionDB.getConn().prepareStatement("select ds_caminho_relatorio from pessoa where id_pessoa = 1");
			ResultSet rsPessoa = stPessoa.executeQuery();
			if (rsPessoa.next()) {
				String caminho = rsPessoa.getString("ds_caminho_relatorio");

				if (relatorio == RELATORIO_DOCUMENTO) {
					caminho += "/" + relatorio + "/" + loginBean.getIdPessoa() + "/xlsx/";
				} else {
					caminho += "/" + relatorio + "/" + loginBean.getIdPessoa() + "/";
				}

				File fileCaminho = new File(caminho);
				if (fileCaminho.exists()) {

					String[] files = fileCaminho.list(new FilenameFilter() {

						@Override
						public boolean accept(File dir, String name) {
							return name.indexOf(".jrxml") > -1;
						}
					});

					if (files != null && files.length > 0) {
						return Response.ok("{\"erro\":false, \"filename\":\"" + files[0] + "\"}").build();
					}

				}

			}

		} finally {
			if (stPessoa != null) {
				stPessoa.close();
			}
			if (connectionDB != null) {
				connectionDB.close();
			}
		}

		return Response.ok("{\"erro\":false}").build();
	}
}

package presentacion.controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.util.List;
import javax.swing.JOptionPane;
import modelo.Agenda;
import modelo.Localidad;
import modelo.Pais;
import modelo.Provincia;
import modelo.TipoContacto;
import persistencia.dao.mysql.DAOSQLFactory;
import presentacion.reportes.ReporteAgenda;
import presentacion.vista.VentanaEditarLocalidad;
import presentacion.vista.VentanaPersona;
import presentacion.vista.VentanaTipoContacto;
import presentacion.vista.Vista;
import dto.Domicilio;
import dto.LocalidadDTO;
import dto.PaisDTO;
import dto.PersonaDTO;
import dto.ProvinciaDTO;
import dto.TipoContactoDTO;

public class Controlador implements ActionListener {
	private Vista vista;
	private List<PersonaDTO> personasEnTabla;
	private List<TipoContactoDTO> tipoContactoEnTabla;
	private VentanaPersona ventanaPersona;
	private VentanaTipoContacto ventanaTipoContacto;
	private Agenda agenda;
	private TipoContacto tipoContacto;
	private VentanaEditarLocalidad ventanaLocalidad;
	int filaSeleccionada;
	private Pais pais;
	private List<PaisDTO> paisEnTabla;
	private Provincia provincia;
	private Localidad localidad;
	private List<ProvinciaDTO> provinciaEnTabla;
	private List<LocalidadDTO> localidadEnTabla;

	public Controlador(Vista vista, Agenda agenda) {
		this.ventanaTipoContacto = new VentanaTipoContacto();
		this.ventanaTipoContacto.getBtnAgregar().addActionListener(a -> agregarTipoContacto(a));
		this.ventanaTipoContacto.getBtnEditar().addActionListener(e -> editarTipoContacto(e));
		this.ventanaTipoContacto.getBtnBorrar().addActionListener(b -> borrarTipoContacto(b));
		this.ventanaTipoContacto.getBtnSalir().addActionListener(s -> salirTipoContacto(s));

		this.ventanaLocalidad = new VentanaEditarLocalidad();
		this.ventanaLocalidad.getBtnAgregar().addActionListener(g -> agregarLocalidad(g));
		this.ventanaLocalidad.getBtnEditar().addActionListener(h -> editarPais(h));
		this.ventanaLocalidad.getBtnBorrar().addActionListener(j -> borrarPais(j));
		this.ventanaLocalidad.getBtnSalir().addActionListener(k -> salirLocalidad(k));

		this.ventanaLocalidad.getComboBox().addActionListener(x -> obtenerSeleccionCombo(x));
		this.ventanaLocalidad.getBtnAgregar().addActionListener(y -> guardarLocalidad(y));

		this.tipoContacto = new TipoContacto(new DAOSQLFactory());
		this.refrescarTablaTipoContacto();

		this.pais = new Pais(new DAOSQLFactory());
		this.refrescarTablaPais();

		this.provincia = new Provincia(new DAOSQLFactory());
		// this.refrescarTablaProvincia();

		this.localidad = new Localidad(new DAOSQLFactory());
		// this.refrescarTablaLocalidad();

		this.vista = vista;
		this.vista.getBtnAgregar().addActionListener(a -> ventanaAgregarPersona(a));
		this.vista.getBtnBorrar().addActionListener(s -> borrarPersona(s));
		this.vista.getBtnReporte().addActionListener(r -> mostrarReporte(r));

		this.ventanaPersona = VentanaPersona.getInstance();
		this.vista.getBtnEditar().addActionListener(e -> mostrarVentanaEditar(e));
		this.ventanaPersona.getBtnAceptar().addActionListener(a -> editarPersona(a));
		this.ventanaPersona.getBtnCancelar().addActionListener(c -> cerrarVentanaEditar(c));

		this.ventanaPersona.getBtnAgregarPersona().addActionListener(p -> guardarPersona(p));
		this.ventanaPersona.getBtnEditarTipoContacto().addActionListener(t -> ventanaEditarTipoContacto(t));

		this.ventanaPersona.getBtnEditarLocalidad().addActionListener(z -> ventanaEditarLocalidad(z));
		this.agenda = agenda;
	}

	private Object ventanaEditarLocalidad(ActionEvent z) {
		this.ventanaLocalidad.mostrarVentana();
		return null;
	}

	private Object obtenerSeleccionCombo(ActionEvent x) {
		this.ventanaLocalidad.obtenerSeleccion();
		return null;
	}

	private Object guardarLocalidad(ActionEvent y) {
		this.ventanaLocalidad.guardarSeleccion();
		return null;
	}

	private void agregarTipoContacto(ActionEvent a) {
		String nombreTipoContacto = this.ventanaTipoContacto.getTxtTipoContacto().getText();
		TipoContactoDTO nuevoTipoContacto = new TipoContactoDTO(0, nombreTipoContacto);
		this.tipoContacto.agregarTipoContacto(nuevoTipoContacto);
		this.refrescarTablaTipoContacto();
		this.ventanaTipoContacto.limpiarTxtTipoContacto();
		refrescarCbTipoContacto();
	}

	public void inicializar() {
		this.refrescarTabla();
		this.vista.show();
	}

	private void ventanaAgregarPersona(ActionEvent a) {
		this.ventanaPersona.limpiarValores();
		this.ventanaPersona.mostrarVentana(tipoContactoEnTabla);
	}

	private void ventanaEditarTipoContacto(ActionEvent t) {
		this.ventanaTipoContacto.mostrarVentana();
	}

	private void editarTipoContacto(ActionEvent e) {
		if (this.seSeleccionoTablaTipoContacto()) {

			int filaSeleccionado = this.ventanaTipoContacto.tablaTipoContactoSeleccionada();
			int idModificar = this.tipoContactoEnTabla.get(filaSeleccionado).getIdTipoContacto();
			System.out.println(idModificar);
			String nombreNuevo = ventanaTipoContacto.getTxtTipoContacto().getText();

			TipoContactoDTO datosNuevos = new TipoContactoDTO(0, nombreNuevo);
			tipoContacto.editarTipoContacto(idModificar, datosNuevos);
			this.refrescarTablaTipoContacto();
		}
		this.ventanaTipoContacto.limpiarTxtTipoContacto();
		refrescarCbTipoContacto();
	}
	
	private void borrarTipoContacto(ActionEvent b) {
		int[] filasSeleccionadas = this.ventanaTipoContacto.getTablaTipoContacto().getSelectedRows();
		for (int fila : filasSeleccionadas) {
			this.tipoContacto.borrarTipoContacto(this.tipoContactoEnTabla.get(fila));
		}
		this.refrescarTablaTipoContacto();
		this.ventanaTipoContacto.limpiarTxtTipoContacto();
		refrescarCbTipoContacto();
	}

	private void salirTipoContacto(ActionEvent s) {
		this.ventanaTipoContacto.cerrar();
		this.ventanaTipoContacto.limpiarTxtTipoContacto();
		refrescarCbTipoContacto();
	}

	private void refrescarTablaTipoContacto() {
		this.tipoContactoEnTabla = tipoContacto.obtenerTipoContacto();
		this.ventanaTipoContacto.llenarTabla(tipoContactoEnTabla);
	}
	
	private void agregarLocalidad(ActionEvent a) {
		String nombreLocalidad = this.ventanaLocalidad.getTxtFieldNombreLocalidad().getText();
		String comboBox = (String) this.ventanaLocalidad.getComboBox().getSelectedItem();

		if (comboBox == "Pais") {
			PaisDTO nuevaLocalidad = new PaisDTO(0, nombreLocalidad);
			this.pais.agregarPais(nuevaLocalidad);
			this.refrescarTablaPais();
			this.ventanaLocalidad.limpiarTodosTxt();
		} else if (comboBox == "Provincia") {
			int idPais = Integer.parseInt((String) this.ventanaLocalidad.getTxtFieldId().getText());
			ProvinciaDTO nuevaLocalidad = new ProvinciaDTO(0, nombreLocalidad, idPais);
			this.provincia.agregarProvincia(nuevaLocalidad);
			this.refrescarTablaProvincia();
			this.ventanaLocalidad.limpiarTodosTxt();
		}
//			else if(comboBox == "Localidad") {
//				
//				LocalidadDTO nuevaLocalidad = new LocalidadDTO(0, nombreLocalidad);
//				this.localidad.agregarLocalidad(nuevaLocalidad);
//				this.refrescarTablaLocalidad();
//				this.ventanaLocalidad.limpiarTodosTxt();
//			}
		else {
			return;
		}
	}

	private void editarPais(ActionEvent e) {
		int filaSeleccionado = this.ventanaLocalidad.tablaPaisSeleccionada();
		int idModificar = this.paisEnTabla.get(filaSeleccionado).getIdPais();
		String nombreNuevo = ventanaLocalidad.getTxtFieldNombreLocalidad().getText();

		PaisDTO datosNuevos = new PaisDTO(0, nombreNuevo);
		pais.editarPais(idModificar, datosNuevos);
		this.refrescarTablaPais();

	}

	private void borrarPais(ActionEvent b) {
		int[] filasSeleccionadas = this.ventanaLocalidad.getTablaPais().getSelectedRows();
		for (int fila : filasSeleccionadas) {
			this.pais.borrarPais(this.paisEnTabla.get(fila));
		}
		this.refrescarTablaPais();
		this.ventanaLocalidad.limpiarTodosTxt();
	}

	private void salirLocalidad(ActionEvent s) {
		this.ventanaLocalidad.cerrar();
		this.ventanaLocalidad.limpiarTodosTxt();
	}

	public void refrescarTablaPais() {
		this.paisEnTabla = pais.obtenerPais();
		this.ventanaLocalidad.llenarTablaPais(paisEnTabla);
	}

	public void refrescarTablaProvincia() {
		this.provinciaEnTabla = provincia.obtenerProvincia();
		this.ventanaLocalidad.llenarTablaProvincia(provinciaEnTabla);
	}

	public void refrescarTablaLocalidad() {
		this.localidadEnTabla = localidad.obtenerLocalidad();
		this.ventanaLocalidad.llenarTablaLocalidad(localidadEnTabla);
	}


	private void guardarPersona(ActionEvent p) {
		PersonaDTO nuevaPersona = obtenerPersonaDeVista();
		if (todosLosCamposSonValidos(nuevaPersona)) {
			this.agenda.agregarPersona(nuevaPersona);
			this.refrescarTabla();
			this.ventanaPersona.cerrar();
		}
	}

	private void borrarPersona(ActionEvent s) {
		int[] filasSeleccionadas = this.vista.getTablaPersonas().getSelectedRows();
		for (int fila : filasSeleccionadas) {
			this.agenda.borrarPersona(this.personasEnTabla.get(fila));
		}
		this.refrescarTabla();
	}

	private void mostrarVentanaEditar(ActionEvent e) {
		filaSeleccionada = this.vista.getTablaPersonas().getSelectedRow();
		if (filaSeleccionada == -1) {
			JOptionPane.showMessageDialog(null, "No ha seleccionado ninguna persona para editar");
			return;
		}
		this.ventanaPersona.mostrarVentanaConValores(this.personasEnTabla.get(filaSeleccionada), tipoContactoEnTabla,paisEnTabla,provinciaEnTabla,localidadEnTabla);
	}

	private void editarPersona(ActionEvent e) {
		int idModificar = this.personasEnTabla.get(filaSeleccionada).getIdPersona();
		PersonaDTO datosNuevos = obtenerPersonaDeVista();
		if (todosLosCamposSonValidos(datosNuevos)) {
			agenda.editarPersona(idModificar, datosNuevos);
			refrescarTabla();
			ventanaPersona.cerrar();
		}
	}

	private void cerrarVentanaEditar(ActionEvent e) {
		this.ventanaPersona.cerrar();
	}

	private void refrescarTabla() {
		this.personasEnTabla = agenda.obtenerPersonas();
		this.tipoContactoEnTabla = agenda.obtenerTiposDeContactos();
		this.vista.llenarTabla(this.personasEnTabla);
	}

	private void refrescarCbTipoContacto() {
		this.ventanaPersona.escribirComboBoxTipoDeContacto(this.tipoContactoEnTabla);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}

	private void mostrarReporte(ActionEvent r) {
		ReporteAgenda reporte = new ReporteAgenda(agenda.obtenerPersonas());
		reporte.mostrar();
	}

	private PersonaDTO obtenerPersonaDeVista() {
		String nombre = ventanaPersona.getTxtNombre().getText();
		String telefono = ventanaPersona.getTxtTelefono().getText();
		String calle = ventanaPersona.getCalle().getText();
		String altura = ventanaPersona.getAltura().getText();
		String piso = ventanaPersona.getPiso().getText();
		String departamento = ventanaPersona.getDepartamento().getText();
		String email = ventanaPersona.getEmail().getText();
		java.sql.Date fechaDeCumpleanios = new java.sql.Date(ventanaPersona.getFechaCumpleanios().getDate().getTime());
		Domicilio domicilio = new Domicilio(calle, altura, piso, departamento);
		String tipoContacto = ventanaPersona.getTipoDeContactoSeleccionado();
		String pais = ventanaPersona.getPaisSeleccionado();
		String provincia = ventanaPersona.getProvinciaSeleccionado();
		String localidad = ventanaPersona.getLocalidadSeleccionado();
		
		return new PersonaDTO(0, nombre, telefono, domicilio, email, fechaDeCumpleanios, tipoContacto,pais,provincia,localidad);
	}

	private boolean seSeleccionoTablaTipoContacto() {
		if (this.ventanaTipoContacto.getTablaTipoContacto().getSelectedRow() != -1) {
			return true;
		}
		return false;
	}

	private boolean todosLosCamposSonValidos(PersonaDTO datosNuevos) {
		String nombre = datosNuevos.getNombre();
		boolean expresionNombre = nombre.matches("[\\w\\s&&[^\\d]]{1,45}");
		if (!expresionNombre) {
			JOptionPane.showMessageDialog(null, "Por favor complete los caracteres de nombre correctamente");
			return false;
		}
		String telefono = datosNuevos.getTelefono();
		boolean expresionTelefono = telefono.matches("[\\d&&[^a-zA-Z]]{10,20}");
		if (!expresionTelefono) {
			JOptionPane.showMessageDialog(null, "Por favor ingrese un teléfono válido");
			return false;
		}
		String calle = datosNuevos.getDomicilio().getCalle();
		boolean expresionCalle = calle.matches("[\\w\\s&&[^\\d]]{1,45}");
		if (!expresionCalle) {
			JOptionPane.showMessageDialog(null, "Por favor ingrese una calle válida");
			return false;
		}
		String altura = datosNuevos.getDomicilio().getAltura();
		boolean expresionAltura = altura.matches("[\\d&&[^a-zA-Z]]+");
		if (!expresionAltura) {
			JOptionPane.showMessageDialog(null, "Por favor ingrese una altura válida");
			return false;
		}
		String piso = datosNuevos.getDomicilio().getPiso();
		boolean expresionPiso = piso.matches("[\\d&&[^a-zA-Z]]*");
		if (!expresionPiso) {
			JOptionPane.showMessageDialog(null, "Por favor ingrese un piso válido");
			return false;
		}

		String departamento = datosNuevos.getDomicilio().getDepartamento();
		boolean expresionDepartamento = departamento.matches("[\\d&&[^a-zA-Z]]*");
		if (!expresionDepartamento) {
			JOptionPane.showMessageDialog(null, "Por favor ingrese un departamento válido");
			return false;
		}

		String mail = datosNuevos.getEmail();
		boolean expresionMail = mail
				.matches("^[A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
		if (!expresionMail) {
			JOptionPane.showMessageDialog(null, "Por favor ingrese una dirección de mail válida");
			return false;
		}
		Date fechaCumpleanios = datosNuevos.getFechaDeCumpleanios();
		if (fechaCumpleanios == null) {
			JOptionPane.showMessageDialog(null, "Por favor seleccione una fecha de cumpleaños");
			return false;
		}
		return true;
	}
}

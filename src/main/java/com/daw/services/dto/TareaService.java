package com.daw.services.dto;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.daw.persistence.entities.Tarea;
import com.daw.persistence.entities.enums.Estado;
import com.daw.persistence.repositories.TareaRepository;
import com.daw.services.exception.TareaNotFoundException;

@Service
public class TareaService {

	@Autowired
	private TareaRepository tareaRepository;

	public List<Tarea> findALL() {
		return this.tareaRepository.findAll();
	}

	public Tarea findById(int idTarea) {
		if (!this.tareaRepository.existsById(idTarea)) {
			throw new TareaNotFoundException("El ID de la tarea no existe");
		}
		
		return this.tareaRepository.findById(idTarea).get();
	}

	public boolean existsById(int idTarea) {
		return this.tareaRepository.existsById(idTarea);
	}

	public boolean deleteById(int idTarea) {
		boolean result = false;

		if (this.tareaRepository.existsById(idTarea)) {
			this.tareaRepository.deleteById(idTarea);
			result = true;

		}

		return result;
	}

	public Tarea create(Tarea tarea) {
		tarea.setFechaCreacion(LocalDate.now());
		tarea.setEstado(Estado.PENDIENTE);

		return this.tareaRepository.save(tarea); // guardar tarea en BBDD

	}

	public Tarea update(Tarea tarea) {
		Tarea tareaBD = this.findById(tarea.getId());
		tarea.setFechaCreacion(tareaBD.getFechaCreacion());
		tarea.setEstado(tareaBD.getEstado());

		return this.tareaRepository.save(tarea);
	}

}

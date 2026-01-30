package models

case class EventoPolitico(
                           id: Int,
                           candidato: String,
                           partidoPolitico: String,
                           evento: String,
                           fechaEvento: String,
                           ubicacion: String,
                           asistentesEstimados: Int,
                           campanaActiva: Boolean
                         )

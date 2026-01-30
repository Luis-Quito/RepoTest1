package models

import doobie._
import doobie.implicits._
import cats.effect._
import cats.implicits._
import scala.io.Source
import models.EventoPolitico

object TallerPolitica extends IOApp.Simple {

  // Configuración de la conexión a MySQL
  val xa = Transactor.fromDriverManager[IO](
    driver = "com.mysql.cj.jdbc.Driver",
    url = "jdbc:mysql://localhost:3306/politica_db",
    user = "root",
    password = "1106050550.utpL",
    logHandler = None
  )

  // Insertar un evento político en la base de datos
  def insertEventoPolitico(
                            id: Int,
                            candidato: String,
                            partidoPolitico: String,
                            evento: String,
                            fechaEvento: String,
                            ubicacion: String,
                            asistentesEstimados: Int,
                            campanaActiva: Boolean
                          ): ConnectionIO[Int] =

    sql"""INSERT INTO eventos_politicos
          (id, candidato, partido_politico, evento, fecha_evento, ubicacion, asistentes_estimados, campana_activa)
          VALUES ($id, $candidato, $partidoPolitico, $evento, $fechaEvento, $ubicacion, $asistentesEstimados, $campanaActiva)
       """.update.run

  // Convertir string "True"/"False" a Boolean
  def parseBoolean(str: String): Boolean = str.trim.equalsIgnoreCase("True")

  override def run: IO[Unit] = {
    // ⚠️ CAMBIAR ESTA RUTA POR LA RUTA DE TU ARCHIVO CSV
    val rutaArchivo = "C:\\Programación\\Prueba\\src\\main\\resources\\politica.csv"

    // Leer el archivo CSV
    val lineasCsv = Source.fromFile(rutaArchivo).getLines().drop(1).toList

    // Crear las operaciones de inserción para cada línea del CSV
    val operacionesDeInsercion = lineasCsv.map { linea =>
      val cols = linea.split(",")
      insertEventoPolitico(
        id = cols(0).trim.toInt,
        candidato = cols(1).trim,
        partidoPolitico = cols(2).trim,
        evento = cols(3).trim,
        fechaEvento = cols(4).trim,
        ubicacion = cols(5).trim,
        asistentesEstimados = cols(6).trim.toInt,
        campanaActiva = parseBoolean(cols(7).trim)
      )
    }

    operacionesDeInsercion.sequence.transact(xa).flatMap { resultados =>
      val totalInsertados = resultados.sum
      IO.println(s" Se insertaron exitosamente $totalInsertados registros en la base de datos")
    }
  }
}
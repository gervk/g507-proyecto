/* 
 Nombre:      sensorCorriente.cpp
 Descripcion: Implementacion de clase para el control del sensor de corriente
*/

#include "sensorCorriente.h"

SensorCorrienteClass::SensorCorrienteClass(float s_ratio, float freq_factor, float tension, String NO_Serie)
{
	S_Ratio = s_ratio;
	ff = freq_factor;
	vV = tension;
	retardo = 2000;				//Por defecto sensamos cada 2 segundos
	retardoEnvioDatos = 5000;	//Por defecto enviamos los datos al servidor cada 5 segundos
	showLog = false;
	log = &Serial;
	analogPin = A0;
	nroSerie = NO_Serie;
	pKWacum = 0;
	ttUltimaLectura = 0;
	ttUltimoEnvio = 0;

	log->begin(115200);
}

//------------------------------------------------------------------------//
float SensorCorrienteClass::leerSensor(float fc)
{

	int ni = 35;          // n. de iteraciones => smooth
						  //  (ni) => rango 10 a 50 mejor promedio [smoothing]
	float retorno = 0.0;

	for (int x = 0; x< ni; x++) {
		do {                         // espero paso por cero  
			delayMicroseconds(100);
		} while (analogRead(0) != 0);

		delay(ff);            // espera centro de ciclo
		delay(10);            // estabilizacion CAD

		retorno = retorno + (analogRead(0)*fc);
	}

	return retorno / ni;
}

//------------------------------------------------------------------------//
void SensorCorrienteClass::setRetardo(int ret)
{
	retardo = ret * 1000;
}

//------------------------------------------------------------------------//
void SensorCorrienteClass::setRetardoEnvioDatos(int ret)
{
	retardoEnvioDatos = ret * 1000;
}

//------------------------------------------------------------------------//
void SensorCorrienteClass::setLogSerial(HardwareSerial *serial, unsigned long velocidad)
{
	log = serial;
	log->begin(velocidad);
}

//------------------------------------------------------------------------//
void SensorCorrienteClass::iniciarSensado()
{
	if(millis() - ttUltimaLectura >= retardo)
	{
		lectura = this->leerSensor(1) / 1.41;    // lectura (rms)   
		vS = (lectura * 0.0048);				 // valor de C.A.D.
		iA = (lectura * S_Ratio) / 1000;		 // Intensidad (A)

		if (iA < 0.003)	//Elimina lecturas erroneas
			iA = 0;

		pKW = (vV * iA) / 1000;					 // Potencia (kW)

		ttTranscurrido = millis() - ttUltimaLectura;	//Tiempo transacurrido entre lecturas
		ttUltimaLectura = millis();						//Actualizamos tiempo de ultima lectura

		//pKWacum = pKWacum + (pKW * (1 / 3600) * (ttTranscurrido / 1000));		//Potencia en KW-hs acumulada. (1/3600) = una hora. (ttTranscurrido / 1000) = tiempo transcurrido en segundos
		pKWacum = pKWacum + (pKW * (ttTranscurrido / 1000)); //Acumulamos en segundos
		
		if (showLog)
			this->printLog();
	}
}

//------------------------------------------------------------------------//
void SensorCorrienteClass::setDisplayLog(bool displayLog)
{
	showLog = displayLog;
}

//------------------------------------------------------------------------//
void SensorCorrienteClass::printLog()
{
	log->print("\n");
	log->print("\n=================================");
	log->print("\n *** PowerCheck - EnerSaving *** ");
	log->print("\n=================================\n");
	log->print("\n- Tension predefinida  [V] --> ");
	log->print(vV, 0);
	log->print("\n- Analog Read --> ");
	log->print(lectura, 10);
	log->print("\n- Lectura del sensor   [V] --> ");
	log->print(vS, 10);
	log->print("\n- Intensidad calculada [A] --> ");
	log->print(iA, 10);
	log->print("\n- Potencia calculada  [kW] --> ");
	log->print(pKW, 10);
	log->print("\n- Potencia acumulada  [kW-hs] --> ");
	log->print(pKWacum, 10);
	log->print("\n-------------------------------\n");
}

//------------------------------------------------------------------------//
void SensorCorrienteClass::setConexionRed(ConexionRedClass *conexion)
{
	conexionRed = conexion;
}


//------------------------------------------------------------------------//
void SensorCorrienteClass::enviarConsumo()
{
	if (((millis() - ttUltimoEnvio) >= retardoEnvioDatos) && pKWacum != 0)
	{
		conexionRed->clearUrl();
		
		//Agregar parametros con los datos del sistema y el consumo actual
		conexionRed->addParameter("codigo_arduino", nroSerie);
		conexionRed->addParameter("tipo_consumo", TIPO_CONSUMO);
		conexionRed->addParameter("consumo", pKWacum);

		//Enviar datos al servidor por medio de la api "consumo"
		if (conexionRed->sendHttpData("api/v1/consumo"))
		{
			ttUltimoEnvio = millis();
			pKWacum = 0;
		}
	}
}


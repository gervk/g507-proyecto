/*
Nombre:      sensorCorriente.h
Descripcion: Definicion de clase para el control del sensor de corriente
*/

#ifndef _SENSORCORRIENTE_h
#define _SENSORCORRIENTE_h

#if defined(ARDUINO) && ARDUINO >= 100
	#include "arduino.h"
#else
	#include "WProgram.h"
#endif

#include "conexionRed.h"

#define TIPO_CONSUMO "1"

class SensorCorrienteClass
{
 protected:
	 float lectura, ff, vV, vS, S_Ratio;
	 double iA, pKW, pKWacum;
	 unsigned long ttUltimaLectura, ttTranscurrido, ttUltimoEnvio;
	 int retardo, retardoEnvioDatos, analogPin;
	 HardwareSerial *log;
	 ConexionRedClass *conexionRed;
	 bool showLog;
	 String nroSerie;

	 float leerSensor(float fc);

 public:
	 SensorCorrienteClass(float s_ratio, float freq_factor, float tension, String NO_Serie);
	 void setRetardo(int ret);
	 void setRetardoEnvioDatos(int ret);
	 void iniciarSensado();
	 void printLog();
	 void setDisplayLog(bool displayLog);
	 void setLogSerial(HardwareSerial *serial, unsigned long velocidad);
	 void setConexionRed(ConexionRedClass *conexion);
	 void enviarConsumo();
};

#endif


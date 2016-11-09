// sensorAgua.h

#ifndef _SENSORAGUA_h
#define _SENSORAGUA_h

#if defined(ARDUINO) && ARDUINO >= 100
	#include "arduino.h"
#else
	#include "WProgram.h"
#endif

#include "conexionRed.h"

#define TIPO_CONSUMO "2"

class SensorAguaClass
{
 protected:
	 String nroSerie;
	 float factCal;
	 float flowRate;
	 unsigned int flowMilliLitres;
	 unsigned long totalMilliLitres;
	 double totalLitros;
	 unsigned long oldTime;
	 unsigned long ttUltimoEnvio;
	 byte sensorInterrupt;
	 bool logPrint;
	 HardwareSerial *log;
	 int retardoEnvioDatos;
	 ConexionRedClass *conexionRed;
	 byte pulseCount;
	 void (*funcionInterrupcion)(void);

	 void printLog();	 

 public:
	 SensorAguaClass(byte sensorPin, byte interruptPin, float factorCalibracion, void(*)(void), String NO_Serie);
	 void calcularConsumo();
	 void setDisplayLog(bool display);
	 void setLogSerial(HardwareSerial *serial, unsigned long velocidad);
	 void setRetardoEnvioDatos(int ret);
	 void setConexionRed(ConexionRedClass *conexion);
	 void contadorPulsos();
	 void enviarConsumo();
};

#endif


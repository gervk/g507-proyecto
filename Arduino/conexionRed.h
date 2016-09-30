/*
Nombre:      conexionRed.h
Descripcion: Definicion de clase para el control de la conexion de Red
*/

#ifndef _CONEXIONRED_h
#define _CONEXIONRED_h

#if defined(ARDUINO) && ARDUINO >= 100
	#include "arduino.h"
#else
	#include "WProgram.h"
#endif

class ConexionRedClass
{
protected:
	HardwareSerial *wifi;
	HardwareSerial *log;
	String urlParameters, httpServer, httpPort;
	bool showLog;

	void cleanWiFiBuffer();

public:
	ConexionRedClass(HardwareSerial *puertoWifi, unsigned long velocidad, String modo);
	bool conectarModulo();
	bool establecerModo(String modo);
	void configHttpServer(String server, String port);
	bool configurarRed(String ssid, String password);
	void setLogSerial(HardwareSerial *serial, unsigned long velocidad);
	void setDisplayLog(bool displayLog);
	void printLog(String message);
	void addParameter(String name, int value);
	void addParameter(String name, unsigned long value);
	void addParameter(String name, float value);
	void addParameter(String name, double value);
	void addParameter(String name, String value);
	bool sendHttpData(String service);
	void manualWifiConfig();
	void clearUrl();
	void resetWifi();
};

#endif

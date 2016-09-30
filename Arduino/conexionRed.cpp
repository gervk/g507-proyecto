/*
Nombre:      conexionRed.cpp
Descripcion: Implementacion de clase para el control de la conexion de Red
*/

#include "conexionRed.h"

ConexionRedClass::ConexionRedClass(HardwareSerial *puertoWifi, unsigned long velocidad, String modo)
{
	wifi = puertoWifi;
	wifi->begin(velocidad);

	this->establecerModo(modo);

	log = &Serial;
	log->begin(115200);

	showLog = false;
}

//------------------------------------------------------------------------//
void ConexionRedClass::configHttpServer(String server, String port)
{
	httpServer = server;
	httpPort = port;
}

void ConexionRedClass::resetWifi()
{
	this->cleanWiFiBuffer();

	wifi->print("AT+RST\r\n");

	while (1) {
		if (wifi->available() > 0)
		{
			if (wifi->find("ready")) {
				printLog("Modulo WiFi reseteado");
				break;
			}
		}
	}

}

//------------------------------------------------------------------------//
bool ConexionRedClass::conectarModulo()
{
	//this->resetWifi();

	this->cleanWiFiBuffer();

	wifi->println("ATE0");
	delay(1000);

	this->cleanWiFiBuffer();

	wifi->println("AT");
	delay(1000);

	if (wifi->find("OK"))
	{
		printLog("Conectado con el modulo Wifi");
		return true;
	}
	else
	{
		printLog("Error de conexion con el modulo WiFi");
		return false;
	}
}

//------------------------------------------------------------------------//
bool ConexionRedClass::establecerModo(String modo)
{
	String cmd;

	cmd = "AT+CWMODE=";
	cmd += modo;

	wifi->println(cmd);
	delay(1000);

	return wifi->find("OK") ? true : false;
}

//------------------------------------------------------------------------//
void ConexionRedClass::cleanWiFiBuffer()
{
	while (wifi->available() > 0 && wifi->read() != -1);
}

//------------------------------------------------------------------------//
bool ConexionRedClass::configurarRed(String ssid, String password)
{
	String cmd;

	cmd = "AT+CWJAP=\"";
	cmd += ssid;
	cmd += "\",\"";
	cmd += password;
	cmd += "\"";

	wifi->println(cmd);
	delay(5000);

	if (!wifi->find("OK"))
	{
		printLog("Error de conexion con la Red");
		return false;
	}
	else
	{
		printLog("WiFi Configurado OK");
		return true;
		/*
		cmd = "AT+CIPSTA?";
		wifi->println(cmd);
		delay(1000);

		if (wifi->find("OK"))
		{
			printLog("WiFi Configurado OK");
			return true;
		}
		else
		{
			printLog("Error de conexion con la Red");
			return false;
		}*/
	}
}

//------------------------------------------------------------------------//
void ConexionRedClass::addParameter(String name, int value) {
	addParameter(name, String(value));
}
void ConexionRedClass::addParameter(String name, unsigned long value) {
	addParameter(name, String(value));
}
void ConexionRedClass::addParameter(String name, float value) {
	addParameter(name, String(value));
}
void ConexionRedClass::addParameter(String name, double value) {
	addParameter(name, String(value,10));
}
void ConexionRedClass::addParameter(String name, String value) {
	urlParameters += urlParameters.length() == 0 ? "?" : "&";
	urlParameters += name;
	urlParameters += "=";
	urlParameters += value;
}


//------------------------------------------------------------------------//
void ConexionRedClass::setDisplayLog(bool displayLog)
{
	showLog = displayLog;
}

//------------------------------------------------------------------------//
void ConexionRedClass::setLogSerial(HardwareSerial *serial, unsigned long velocidad)
{
	log = serial;
	log->begin(velocidad);
}

//------------------------------------------------------------------------//
void ConexionRedClass::printLog(String message)
{
	if (showLog)
		log->println(message);
}

//------------------------------------------------------------------------//
bool ConexionRedClass::sendHttpData(String service)
{
	String cmd;

	cmd = "AT+CIPSTART=\"TCP\",\"";
	cmd += httpServer;
	cmd += "\",";
	cmd += httpPort;
	wifi->println(cmd);
	delay(2000);

	if (wifi->find("ERROR"))
	{
		printLog("No se pudo conectar con el servidor HTTP:");
		printLog(cmd);
		return false;
	}

	cmd = "POST /";
	cmd += service;
	cmd += urlParameters;
	cmd += " HTTP/1.1\r\n";
	cmd += "Host: ";
	cmd += httpServer;
	cmd += "\r\n\r\n";
	
	wifi->print("AT+CIPSEND=");
	wifi->println(cmd.length());

	if (wifi->find(">"))
		wifi->print(cmd);
	else
	{
		printLog("No se pudieron enviar los datos");
		printLog(cmd);
		printLog((String)cmd.length());
	}

	this->clearUrl();
	
	for (int i=0; i<8; i++)
	{
		if (wifi->available() > 0)
		{
			if (wifi->find("OK"))
			{
				wifi->println("AT+CIPCLOSE");
				return true;
			}
		}

		delay(1000);
	}

	wifi->println("AT+CIPCLOSE");
	printLog("Error al enviar los datos");
	return false;	
}


//------------------------------------------------------------------------//
void ConexionRedClass::manualWifiConfig()
{
	if (wifi->available() > 0) {
		log->print((char)wifi->read());
	}

	if (log->available() > 0) {
		wifi->print((char)log->read());
	}
}

void ConexionRedClass::clearUrl()
{
	urlParameters = "";
}


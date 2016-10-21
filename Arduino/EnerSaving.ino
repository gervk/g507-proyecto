/*
 Name:		EnerSaving.ino
 Created:	7/31/2016 9:49:47 PM
*/


#include "conexionRed.h"
#include "sensorCorriente.h"

// -- variables and pins definition ------------------
#define NRO_SERIE		"1723000001"
#define MODO_ESTACION	"1"
#define MODO_AP			"2"
#define MODO_MIXTO		"3"

SensorCorrienteClass *sensorCorriente;
ConexionRedClass *conexionRed;
String ssid = "proyecto";
String pass = "enersaving";

// -- initialize serial comm & parameters ------------
void setup() {
	//Inicializamos el sensor de corriente
	sensorCorriente = new SensorCorrienteClass( 
									116.14,			//Sensor/ratio (mA/mV ) => (100/1.05) / (R2/R1 = 9.7/11.82 = 0.82) = 116.14
									5,				//Freq. factor / (50Hz -> 5 / 60Hz -> 4.15)   
									230, 			//Valor de tension a computar
									NRO_SERIE);

	sensorCorriente->setRetardo(2);					//Retardo en segundos entre sensados
	sensorCorriente->setRetardoEnvioDatos(5);		//Retardo en segundos para el envio de datos al servidor
	sensorCorriente->setLogSerial(&Serial,115200);	//Puero Serial para imprimir el log del sensado
	sensorCorriente->setDisplayLog(false);			//Mostrar log
	//sensorCorriente->setDisplayLog(true);

	//Establecemos conexion con el modulo de red
	conexionRed = new ConexionRedClass(&Serial1,115200,MODO_MIXTO);	//Pasamos como parametro el puero Serial donde se encuentra conectado el modulo Wifi
	conexionRed->setLogSerial(&Serial,115200);
	conexionRed->setDisplayLog(false);			
	//conexionRed->setDisplayLog(true);
	//conexionRed->configHttpServer("enersaving-laravel.herokuapp.com","80");	
	conexionRed->configHttpServer("192.168.43.191", "80");

	conexionRed->resetWifi();

	if (conexionRed->conectarModulo())
		conexionRed->configurarRed(ssid,pass);

	sensorCorriente->setConexionRed(conexionRed);
}

int reset = true;

// -- main loop --------------------------------------
void loop() {
	

	if (reset) {
		conexionRed->resetWifi();
		reset = false;
	}

	sensorCorriente->iniciarSensado();

	sensorCorriente->enviarConsumo();

	//conexionRed->manualWifiConfig();
}
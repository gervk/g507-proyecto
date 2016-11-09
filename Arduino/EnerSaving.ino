/*
 Name:		EnerSaving.ino
 Created:	7/31/2016 9:49:47 PM
*/


#include "sensorAgua.h"
#include "conexionRed.h"
#include "sensorCorriente.h"

// -- variables and pins definition ------------------
#define NRO_SERIE		"1723000001"
#define MODO_ESTACION	"1"
#define MODO_AP			"2"
#define MODO_MIXTO		"3"

SensorCorrienteClass *sensorCorriente;
ConexionRedClass *conexionRed;
SensorAguaClass *sensorAgua;

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
	//sensorCorriente->setDisplayLog(true);			//Mostrar log
	
	//Establecemos conexion con el modulo de red
	conexionRed = new ConexionRedClass(&Serial1,115200,MODO_MIXTO);	//Pasamos como parametro el puero Serial 
																    //donde se encuentra conectado el modulo Wifi
	conexionRed->setLogSerial(&Serial,115200);	
	//conexionRed->setDisplayLog(true);
	//conexionRed->configHttpServer("enersaving-laravel.herokuapp.com","80");	
	conexionRed->configHttpServer("192.168.1.113", "80");

	conexionRed->resetWifi();

	if (conexionRed->conectarModulo())
		conexionRed->configurarRed(ssid,pass);

	sensorCorriente->setConexionRed(conexionRed);

	//Inicializar sensor de agua
	sensorAgua = new SensorAguaClass(2,					  //Pin del sensor
									 0,					  //Pin de la interrupcion 
									 7.5,				  //Factor de calibracion
								     funcionInterrupcion, //Funcion que se ejecutara en la interrupcion
									 NRO_SERIE);
	sensorAgua->setLogSerial(&Serial, 115200);
	//sensorAgua->setDisplayLog(true);
	sensorAgua->setRetardoEnvioDatos(15);
	sensorAgua->setConexionRed(conexionRed);
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

	sensorAgua->calcularConsumo();

	//conexionRed->manualWifiConfig();
}

void funcionInterrupcion()
{
	sensorAgua->contadorPulsos();
}
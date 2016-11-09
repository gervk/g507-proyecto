// 
// 
// 

#include "sensorAgua.h"

SensorAguaClass::SensorAguaClass(byte sensorPin, byte interruptPin, float factorCalibracion, void(*funcion)(void), String NO_Serie)
{
	nroSerie = NO_Serie;
	factCal  = factorCalibracion;
	pulseCount = 0;
	flowRate = 0.0;
	flowMilliLitres = 0;
	totalMilliLitres = 0;
	oldTime = 0;
	ttUltimoEnvio = 0;
	totalLitros = 0;
	logPrint = false;
	sensorInterrupt = interruptPin;

	funcionInterrupcion = funcion;

	log = &Serial;
	log->begin(115200);

	pinMode(sensorPin, INPUT);
	digitalWrite(sensorPin, HIGH);

	attachInterrupt(sensorInterrupt, funcionInterrupcion, FALLING);
}

void SensorAguaClass::calcularConsumo() 
{
	double flowLitros;

	if ((millis() - oldTime) > 1000)    // Only process counters once per second
	{
		// Disable the interrupt while calculating flow rate and sending the value to
		// the host
		detachInterrupt(sensorInterrupt);

		// Because this loop may not complete in exactly 1 second intervals we calculate
		// the number of milliseconds that have passed since the last execution and use
		// that to scale the output. We also apply the calibrationFactor to scale the output
		// based on the number of pulses per second per units of measure (litres/minute in
		// this case) coming from the sensor.
		flowRate = ((1000.0 / (millis() - oldTime)) * pulseCount) / factCal;

		// Note the time this processing pass was executed. Note that because we've
		// disabled interrupts the millis() function won't actually be incrementing right
		// at this point, but it will still return the value it was set to just before
		// interrupts went away.
		oldTime = millis();

		// Divide the flow rate in litres/minute by 60 to determine how many litres have
		// passed through the sensor in this 1 second interval, then multiply by 1000 to
		// convert to millilitres.
		flowMilliLitres = (flowRate / 60) * 1000;

		// Acumular en litros
		flowLitros = (flowRate / 60);
		totalLitros += flowLitros;

		// Add the millilitres passed in this second to the cumulative total
		totalMilliLitres += flowMilliLitres;

		if (logPrint)
			printLog();

		// Reset the pulse counter so we can start incrementing again
		pulseCount = 0;

		//Enviar datos
		if (flowLitros == 0)
			enviarConsumo();

		// Enable the interrupt again now that we've finished sending output
		attachInterrupt(sensorInterrupt, funcionInterrupcion, FALLING);
	}
}

void SensorAguaClass::enviarConsumo()
{
	if (((millis() - ttUltimoEnvio) >= retardoEnvioDatos) && totalLitros != 0)
	{
		conexionRed->clearUrl();

		//Agregar parametros con los datos del sistema y el consumo actual
		conexionRed->addParameter("codigo_arduino", nroSerie);
		conexionRed->addParameter("tipo_consumo", TIPO_CONSUMO);
		conexionRed->addParameter("consumo", totalLitros);

		//Enviar datos al servidor por medio de la api "consumo"
		if (conexionRed->sendHttpData("api/v1/consumo"))
		{
			ttUltimoEnvio = millis();
			totalLitros = 0;
		}
	}
}

void SensorAguaClass::printLog()
{
	unsigned int frac;

	// Print the flow rate for this second in litres / minute
	log->print("Flow rate: ");
	log->print(int(flowRate));  // Print the integer part of the variable
	log->print(".");             // Print the decimal point
								   // Determine the fractional part. The 10 multiplier gives us 1 decimal place.
	frac = (flowRate - int(flowRate)) * 10;
	log->print(frac, DEC);      // Print the fractional part of the variable
	log->println(" L/min");

	// Print the number of litres flowed in this second
	log->print("Current Liquid Flowing: ");             // Output separator
	log->print(flowMilliLitres);
	log->println(" mL/Sec");

	// Print the cumulative total of litres flowed since starting
	log->print("Output Liquid Quantity: ");             // Output separator
	log->print(totalMilliLitres);
	log->println(" mL");
	log->println();
}

void SensorAguaClass::setDisplayLog(bool display)
{
	logPrint = display;
}

void SensorAguaClass::setLogSerial(HardwareSerial *serial, unsigned long velocidad)
{
	log = serial;
	log->begin(velocidad);
}

void SensorAguaClass::setRetardoEnvioDatos(int ret)
{
	retardoEnvioDatos = ret * 1000;
}

void SensorAguaClass::setConexionRed(ConexionRedClass *conexion)
{
	conexionRed = conexion;
}

void SensorAguaClass::contadorPulsos()
{
	pulseCount++;
}
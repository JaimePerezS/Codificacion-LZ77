/* Realizado por: Alejandro Martín González y Jaime Pérez Sánchez */
import java.util.ArrayList;

import entrada_salida.LectorBinario;
import estructura_datos.Tupla;

/***********************************************************************************************
 *  Ejecución del codificador Lempel-Ziv: 
 *  	% java PlantillaCodificadorLZ filePath 
 *  
 *  Utilidad: Permite la codificación de un archivo de entrada usando el algoritmo de Lempel-Ziv. 
 *
 ***********************************************************************************************/

public class PlantillaCodificadorLZ {
	
	// El algoritmo de Lempel-Ziv se basa en el uso de dos "ventanas deslizantes contiguas"
	// que se desplazan al unísono sobre la trama a codificar. 
	//
	// Partiendo de un índice que indica la posición actual (pos) al recorrer la trama, existe una 
	// ventana anterior a dicho índice que denominados "ventana de búsqueda" (VentanaBusqueda) y una 
	// ventana posterior a éste que denominamos "ventana de patrones" (VentanaPatrones). La "ventana
	// de patrones" parte de un tamaño inicial definido por la variable tamanyoVentanaPatrones
	// que va disminuyendo hacia 0 cuando el índice de posición (pos) se aproxima al final de la trama.
	// Por su parte la "ventana de búsqueda" parte de un tamaño inicial  de 0 caracteres que va 
	// incrementando hasta un valor fijado por tamanyoVentanaPatrones, a medida que el puntero de 
	// posición se mueve por la trama a codificar.
	
	// Si la trama contiene bastantes patrones/secuencias redundantes (palabras repetidas o frases), 
	// el número de Tuplas en la salida será menor que el número de caracteres en la trama. La clave consiste en 
	// que cada Tupla "apunte" hacia una ocurrencia previa de un patrón de caracteres coincidente. Hay que tener en 
	// cuenta que cada Tupla (en bits) ocupa más que un carácter, dado que guarda tres parámetros: el primer 
	// carácter no coincidente y otros dos parámetros que dependen del tamaño escogido para las ventanas de búsqueda
	// y patrones. Por lo tanto, para que el factor de compresión sea >1 y aceptable el número de tuplas
	// tiene que ser menor que el de caracteres de la trama a codificar, compensando el hecho de que el tamaño de
	// cada Tupla es mayor que el tamaño se un carácter.
	
	// Por ejemplo en español las tramas contienen frecuentemente palabras como "el, y, en, pero, por, a, que, ..."
	
	
	// Atributos:
	private int tamanyoVentanaBusqueda;
	private int tamanyoVentanaPatrones;

	
	// Constructor
	public PlantillaCodificadorLZ() {
		// En el constructor se asigna el tamaño de las ventanas "de búsqueda" y
		// "de patrones" declaradas previamente en los atributos. Los valores
		// óptimos se obtienen sustrayendo 1 a valores en potencia de dos.
		
		this.tamanyoVentanaBusqueda = 31;
		this.tamanyoVentanaPatrones = 7;
		
		// Con estos valores el tamaño de la ventana de búsqueda puede codificarse con 5 bits
		// (si fuera de 32 posiciones se requeriría un bit más). Por su parte la ventana de 
		// patrones se almacena en 3 bits.
		
		// Puede comprobarse como afecta a la codificación cambiar estos tamaños de ventana,
		// lo que influirá directamente también en el tamaño de cada tupla, como se puede 
		// comprobar más adelante durante la construcción de las tuplas.	
	}
	
	
   /*
	* Se lee el archivo de entrada (filePath, a codificar) como secuencia de palabras de 8 bits usando 
	* LectorBinario, después se procede a la codificación con el algoritmo de Lempel-Ziv. 
	* Se obtiene una lista de objetos <Tupla> que representan la trama codificada. 
	* 
	* Esta lista de tuplas puede escribirse en un determinado flujo de salida (por ejemplo un archivo) 
	* en una etapa posterior mediante la clase EscritoBinario u otra utilidad, resultando un archivo comprimido).
	* 
	* @param filePath es ell path del archivo de entrada a codificar.
	* 
	* @return ArrayList<Tupla> que representa los datos codificados como una lista de objetos Tupla. 
	*/
	public ArrayList<Tupla> codificar(String filePath){
		
		// Lista de Tuplas, resultado de la aplicación del algoritmo de codificación
		ArrayList<Tupla> codificacionEnTuplas = new ArrayList<Tupla>();
				
    	// Leer archivo de entrada y almacenar en una cadena (trama)
		LectorBinario lector = new LectorBinario(filePath);
        StringBuilder sb = new StringBuilder();
        while (!lector.esVacio()) {
            char b = lector.leerPalabra();
            sb.append(b); 	// OJO! leerPalabra() devuelve una palabra 
            				// de 8 bits y el tipo char es de 16 bits.
        }
        String trama = sb.toString();
        
        // Índice que define la posicion en la trama
        int pos = 0;
        
        int posicionUltimaCoincidencia = -1;
        int offset = -1;
        
        String patronCoincidente;
        Tupla tuplaActual;
        
        while(pos < trama.length()){
			
			/* Establecer el comienzo de la ventana de búsqueda
			 * Mediante el siguiente bloque if-else, controlamos
			 * el avance de la ventana de búsqueda. En el momento
			 * en el que la posicion actual supere el tamaño
			 * de la ventada de búsqueda, el inicio de la ventana variará
			 * de posición */
        	int inicioVentanaBusqueda;
        	
        	if(pos > tamanyoVentanaBusqueda) {
				inicioVentanaBusqueda = pos - tamanyoVentanaBusqueda;
			}
        	
        	else { 
        		inicioVentanaBusqueda = 0;
        	}
        	
        	/* Establecer el final de la ventana de patrones. En caso de que
        	* la posición del final de la ventana de patrones supere el tamaño de la trama, se
        	* le asignará el tamaño de la trama como índice, evitando salirnos de la trama */
        	int finalVentanaPatrones;
        	
        	if(pos + tamanyoVentanaPatrones < trama.length()) {
        		finalVentanaPatrones = pos + tamanyoVentanaPatrones;
        	} 
        	
        	else {
        		finalVentanaPatrones = trama.length();
        	}
        	
        	/* Mostrar el contenido de la ventana de búsqueda */
        	System.out.print("Ventana de Búsqueda: " + trama.substring(inicioVentanaBusqueda, pos) + "  ");
        	
        	/* (En la misma línea) mostrar el contenido de la ventana de patrones */
        	System.out.print("Ventana de Patrones: " + trama.substring(pos, finalVentanaPatrones) + "  ");
        	
        	/* Obtener el substring que se corresponde a la ventana de búsqueda para buscar una coincidencia.
        	 * (En la primera iteración la ventada de busqueda está vacía, es decir si pos == 0). */
        	String ventanaBusqueda;
        	
        	if(pos == 0) {
        		ventanaBusqueda = "";
        	} 
        	
        	else {
        		ventanaBusqueda = trama.substring(inicioVentanaBusqueda, pos);
        	}
        	
			/* Búsqueda de patrones de coincidencia. Consiste en determinar el substring más largo de la ventana 
        	 * de patrones, (empezando por su primer carácter), para el cual hay un substring coincidente 
			 * en la ventana de búsqueda (ventanaBusqueda) */
			
			
        	/* Si una coincidencia para el primer carácter ha sido encontrada. Ver si el patrón
			 * de coincidencia se extiende a más de un carácter (con un bucle) */
        	
        	
        	/* Inicialización de las variables necesarias a comparar para rellenar la tupla obtenida */
        	int longPatronCoincidente = 0;
        	
        	/* Extraemos el siguiente carácter a la pos actual */
        	patronCoincidente = trama.substring(pos, pos + 1);
        	
        	/* Se comprueba si existe coincidencia en la ventana de búsqueda para dicho carácter */
        	if(ventanaBusqueda.indexOf(patronCoincidente) != -1) {
        		
        		/* Como se ha encontrado una coincidencia, incrementamos la long del patron coincidente */
        		longPatronCoincidente++;
        		
        		/* Este while limita la longitud de la coincidencia al tamaño de la ventana de patrones */
        		while(longPatronCoincidente <= tamanyoVentanaPatrones){
					/* Bucle que va extendiendo la longitud del patrón coincidente hasta que la comprobación falla. */
        			
        			/* Actualización del siguiente carácter a comprobar si coincide */
        			patronCoincidente = trama.substring(pos, pos + longPatronCoincidente + 1);
        			
        			/* Si coincide, se incrementa la longitud del patron coincidente.
        			 * En caso de que no sea así, se sale del bucle. */
        			if((ventanaBusqueda.indexOf(patronCoincidente) != -1)){
        				longPatronCoincidente++;
        			} 
        			
        			else {
        				break;
        			}
        		}
        		
        		/* Obtenemos la última posición en la que se ha producido una coincidencia en la ventana de búsqueda */
        		posicionUltimaCoincidencia = ventanaBusqueda.indexOf(trama.substring(pos, pos + longPatronCoincidente));
        		
        		/* Se incrementa la posición del índice pos para saltar los caracteres coincidentes */
				pos += longPatronCoincidente;

        		/* Se genera la tupla correspondiente con:
				 *	 - El cálculo del offset
        		 *	 - Logitud del patrón coincidente (longPatronCoincidente)
				 *	 - El próximo carácter después del patrón coincidente. */
							
				/* Para el cáculo del offset, obtenemos la "distancia" 
				 * entre la posición actual y la posición de la última coincidencia */
				if(pos - longPatronCoincidente < tamanyoVentanaBusqueda) {
					offset = (pos - longPatronCoincidente) - posicionUltimaCoincidencia;
				} 
				
				else {
					offset = tamanyoVentanaBusqueda - posicionUltimaCoincidencia;
				}
        	}
        	
        	else {
        			/* Por el contrario, si no se obtuvo ninguna coincidencia en la ventana de búsqueda 
        			 * (ni siquiera para el primer carácter de la ventana de patrones). Se construye la tupla 
        			 * con los valores siguientes:
        			 *    - offset = 0
        			 *    - longPatronCoincidente = 0
        			 *    - proximoCaracter: El carácter en la posición actual (pos) que no coincide con 
        			 *                       ninguno de la ventana de búsqueda */
        			offset = 0;
        			longPatronCoincidente = 0;
        	}
        	
        	/* Crear la nueva tupla, guardarla en la lista que representa la codificación de la trama */
			tuplaActual = new Tupla(offset, longPatronCoincidente, trama.substring(pos, pos + 1));
         	codificacionEnTuplas.add(tuplaActual);
        	
        	/* Incrementar en uno la posicion actual */
        	pos++;
        	
        	/* Mostrar el contenido de cada tupla */
        	System.out.println("Tupla:" + " " + tuplaActual);

		} // end while (recorriendo trama)
		
		return codificacionEnTuplas;
	}
	
	public void decodificar(ArrayList<Tupla> tramaCodificada) {
		
		/* Utilizamos un StringBuilder para ir almacenando la cadena decodificada */
		StringBuilder trama = new StringBuilder();
		
		/* Mientras queden objetos en la colección vamos a iterar cogiendo cada tupla 
		 * que contiene:
		 *    - offset
         *    - longPatronCoincidente
         *    - proximoCaracter */
		Tupla tupla;
		
		/* Mediante el siguiente bucle for, recorremos las tuplas resultantes de codificar
		 * la trama */
		for (int i = 0; i < tramaCodificada.size(); i++) {
			
			/* Cogemos la siguiente tupla de la colección en orden que fueron guardadas */
			tupla = tramaCodificada.get(i);
			
			if(tupla.getTamanyoPatron() == 0) {
				
				/* Si era una tupla que no coincidia con ningún patron la longitud del patrón es 0 
				 * solo es necesario coger el carácter guardado en ProximoCaracter que en este caso 
				 * hace referencia al caracter sin coincidencias y añadirlo al StringBuilder.*/
				trama.append(tupla.getProximoCaracter());
			}
			
			else {
				
				/* En este caso necesitamos reconstruir la información del patrón encontrado
				 * por lo tanto vamos iterar en la parte ya reconstruida carácter a carácter 
				 * tantas veces como longitud tenga el patrón. */
				for(int j = 0; j < tupla.getTamanyoPatron(); j++) {
					
					/* Restar la longitud de la trama actual - el offset nos da como
					 * resultado la posición del carácter que necesitamos en la trama actual */
					char siguienteCaracter = trama.charAt(trama.length() - tupla.getOffset());
					
					/* Añadir el siguiente carácter decodificado en al StringBuilder */
					trama.append(siguienteCaracter);
				}
				
				/* Añadimos el siguiente caracter que no pertenece al patrón y que está guardado en la tupla */
				trama.append(tupla.getProximoCaracter());
			}		
		}

		/* Se imprime la secuencia decodificada */
		System.out.println("Trama decodificada: " + trama.toString());
	}

	public static void main(String[] args) {
		if(args.length==1){ // Control de argumentos mejorable!!
			ArrayList<Tupla> t = new PlantillaCodificadorLZ().codificar(args[0]);
			new PlantillaCodificadorLZ().decodificar(t);
		}
	}
}

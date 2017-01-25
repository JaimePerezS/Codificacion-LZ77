# Codificacion-LZ77

*  Ejecución del codificador Lempel-Ziv: 
  	% java PlantillaCodificadorLZ filePath 
    
 *  Utilidad: Permite la codificación de un archivo de entrada usando el algoritmo de Lempel-Ziv. 

El algoritmo de Lempel-Ziv se basa en el uso de dos "ventanas deslizantes contiguas"
que se desplazan al unísono sobre la trama a codificar. 
Partiendo de un índice que indica la posición actual (pos) al recorrer la trama, existe una 
ventana anterior a dicho índice que denominados "ventana de búsqueda" (VentanaBusqueda) y una 
ventana posterior a éste que denominamos "ventana de patrones" (VentanaPatrones). La "ventana
de patrones" parte de un tamaño inicial definido por la variable tamanyoVentanaPatrones
que va disminuyendo hacia 0 cuando el índice de posición (pos) se aproxima al final de la trama.
Por su parte la "ventana de búsqueda" parte de un tamaño inicial  de 0 caracteres que va 
incrementando hasta un valor fijado por tamanyoVentanaPatrones, a medida que el puntero de 
posición se mueve por la trama a codificar.

Si la trama contiene bastantes patrones/secuencias redundantes (palabras repetidas o frases), 
el número de Tuplas en la salida será menor que el número de caracteres en la trama. La clave consiste en 
que cada Tupla "apunte" hacia una ocurrencia previa de un patrón de caracteres coincidente. Hay que tener en 
cuenta que cada Tupla (en bits) ocupa más que un carácter, dado que guarda tres parámetros: el primer 
carácter no coincidente y otros dos parámetros que dependen del tamaño escogido para las ventanas de búsqueda
y patrones. Por lo tanto, para que el factor de compresión sea >1 y aceptable el número de tuplas
tiene que ser menor que el de caracteres de la trama a codificar, compensando el hecho de que el tamaño de
cada Tupla es mayor que el tamaño se un carácter.
	
Por ejemplo en español las tramas contienen frecuentemente palabras como "el, y, en, pero, por, a, que, ..."
	

package estructura_datos;

// Tupla para almacenar de manera conjunta los tres datos críticos  que se 
// producen en cada iteración del algoritmo de codificación de Lempel-Ziv.

public class Tupla{
  // offset que representa la distancia entre el comienzo del patrón coincidente en la ventana 
  // de búsqueda y la posición del índice actual de la trama (pos), tiene un valor = 0 si 
  // no existe patrón coincidente en la ventana de búsqueda.
  private int offset;
  // tamanyoPatron especifica la longitud del patrón coincidente o 0 si no hay coincidencia.
  private int tamanyoPatron;
  // proximoCaracter representa el primer carácter "no coincidente" siguiendo al patrón coincidente, 
  // o simplemente el carácter no coincidente del indice que marca la posición actual (pos).
  private String proximoCaracter;  
  
  // Constructor
  public Tupla(int offset, int stringLen, String proximoCaracter){
    this.offset = offset;
    this.tamanyoPatron = stringLen;
    this.proximoCaracter = proximoCaracter;
  } 
 
  public int getOffset() {
	return offset;
  }

  public void setOffset(int offset) {
	this.offset = offset;
  }
  
  public int getTamanyoPatron() {
	return tamanyoPatron;
  }

  public void setTamanyoPatron(int tamanyoPatron) {
	this.tamanyoPatron = tamanyoPatron;
  }

  public String getProximoCaracter() {
	return proximoCaracter;
  }

  public void setProximoCaracter(String proximoCaracter) {
	this.proximoCaracter = proximoCaracter;
  }

  // Sobreescribir toString()
  public String toString(){
	return offset + "," + tamanyoPatron + "," + proximoCaracter;
  } // end toString
  
} // end class Tupla
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package os.regv.os;

/**
 *
 * @author gabrius
 */
public class Memory {
// Veikimo principas
// Nuo 30 bloka prasideda puslapiu lentele
    private long[][] memory ;

    public Memory(){memory = new long[33][10];}
    
    public long[][] getMemory(){
        return memory;
    }
public long allocate_Memory(){
//Puslpiu bloko adresas
  int paging_adr = 30;      
  
  while ((memory[paging_adr][0] != 0) && (paging_adr < 33)) paging_adr++;  
  //Atmintyje nera laisvos vietos puslapiams
  if (memory[paging_adr][0] != 0) return 0; 
  
  // Vieta kur prasideda programos kodas
  // skiriami 5 blokai
  int progr_adr = (paging_adr % 30) * 10 + 1;
  memory[paging_adr][0] = progr_adr;

  for (int i = 1; i <= 5; ++i){
    memory[paging_adr][i] = 5 + progr_adr++ ;
  }

  return memory[paging_adr][0];
}

//check_Memory, kurio paskirtis - tikrinti ar yra laisvas atminties laukas.
public boolean check_Memory(int x){
  int i, j;
  boolean free = true;
  
  for (i = 30; i < 33; i++){
    for (j = 0; j < 10; j++){
      if ((free) && (x == memory[i][j] - 1)) free = false; 
    }
  }
  return free;
}


//show_Memory - parodo kaip atrodo atmintis 
public void show_Memory(){
  int i, j, k;
  System.out.printf("Bl/Wr");
  for (k = 0; k < 10; k++) System.out.printf("  %d  ", k);
  System.out.printf("\n*******************************************************\n");
  for (i = 0; i < 33; i++){
    if (i < 10) System.out.printf("%d  | ", i);
    else System.out.printf("%d | ", i);
    for (j = 0; j < 10; j++){
      if (memory[i][j] != 0){
        if(i < 30 || (i >= 30 && j > 5)){
          char[] character = { (char)((memory[i][j] & 0xFF000000) / 0x1000000),
                                (char)((memory[i][j] & 0xFF0000) / 0x10000), 
                                (char)((memory[i][j] & 0xFF00) / 0x100), 
                                (char)((memory[i][j] & 0xFF))};

          System.out.printf("%c%c%c%c ", character[0], 
                              character[1], 
                              character[2], 
                              character[3]);
        }else{
          System.out.printf("%-5d", memory[i][j]);
        }
      }else{
        System.out.printf("0    ");
      }
    }
    System.out.printf("\n");
  }
  System.out.printf("\n*******************************************************\n");
}
}

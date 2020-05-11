#include <main.h>
#use rs232(uart1, baud=9600, parity=N, xmit=PIN_C6, rcv=PIN_C7, bits=8, stream=RXTX)
#define TRIGGER PIN_B7
#define ECHO PIN_B3
#define LED_GREEN PIN_C0
#define LED_RED PIN_C3
#define HORLOGE_AFFICHAGE 25
#define RX PIN_C7
#define TX PIN_C6
//#byte timer0low = 0xfd6
char buffer[4];
int i = 0;

int16 distance;
int16 limiteHigh = 24000;
int16 limiteLow = 3000;
int read=0;

int u, d, c;

int16 triggerSensor(){

   int16 i;

   output_low(TRIGGER);
   delay_ms(10);
   output_high(TRIGGER);
   delay_ms(10);
   output_low(TRIGGER);

   while(!input(ECHO)){
     delay_us(10);
   }
   set_timer0(0);

   while(input(ECHO) && (i < 25000)){
      i = get_timer0();
   }
   return i;
}

void affichage(int16 value,int16 range){

   if(value < 100){
    output_d((value/10)+32);
    delay_ms(HORLOGE_AFFICHAGE);
    output_d((value%10)+16);
    delay_ms(HORLOGE_AFFICHAGE);
   }
   else if(value < 1000){
    output_d((value/100)+32+64);
    delay_ms(HORLOGE_AFFICHAGE);
    output_d(((value%100)/10)+16);
    delay_ms(HORLOGE_AFFICHAGE);
   }
   else{
    output_d(4+32+64);
    delay_ms(HORLOGE_AFFICHAGE);
    output_d(2+16);
    delay_ms(HORLOGE_AFFICHAGE);
   }
   printf("%ld", value);
}
/**
Lecture des info venant de JAVA
#int_RDA
void RDA_interrupt(){
   buffer[i] = getc();
   if(buffer[0] == ':' && read== 0){
      i++;
      if(i>=4){
         i=0;
         read=1;
      }
   }
}
**/

void main(){
   output_a(0);
   set_tris_b(2);
   set_tris_d(48);

   setup_low_volt_detect(FALSE);
   setup_timer_0(RTCC_INTERNAL|RTCC_8_BIT|RTCC_DIV_256);

   clear_interrupt(INT_TIMER0);
   enable_interrupts(GLOBAL);
   enable_interrupts(INT_TIMER0);
   enable_interrupts(INT_RDA);
   setup_timer_0 (T0_INTERNAL | T0_DIV_2);

   while(TRUE)
   {
      //lance une impulsion au niveau de la sonde
      output_low(TRIGGER);
      delay_ms(10);
      output_high(TRIGGER);
      delay_us(10);
      output_low(TRIGGER);

      //on attend que l'echo parte
      while(!input(ECHO)){
      }
      set_timer0(0);

      //on attend que l'echo revienne
      while(input(ECHO) && distance < 25000){
      }

      //on récupère le temps, et on le divise par 2 pour avoir un aller
      distance = get_timer0();
      distance= distance/2;

      if (distance > limiteHigh){
        output_high(LED_RED);
        output_low(LED_GREEN);
      }
      else if(distance < limiteLow){
         output_high(LED_RED);
         output_low(LED_GREEN);
      }
      else{
         output_low(LED_RED);
         output_high(LED_GREEN);
      }
      if(read ==1){
         read =0;

         u = buffer[3]-48;
         d = buffer[2]-48;
         c = buffer[1]-48;

         limiteHigh = (c*100+d*10+u);
      }
      affichage(distance/100, distance);
   }


}

#include <stdio.h>

int main(int argc, char* argv[])
{
  char a = '\x8'; // OK, hexa
  char b = '\1'; // NOK
  char c = 0; // OK
  char d = 00; // NOK
  char e = 010; // NOK
  char f = 42; // OK
  char g = +0; // OK
  char h = -0; // OK
  
  printf("\x32\n"); // OK, hexa
  printf("\109\n"); // NOK
  printf("\100\n"); // NOK
  printf("\50x\51\n"); // NOK
  printf("hello world\n"); // NOK
}

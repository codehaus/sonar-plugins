#include <stdio.h>

int main(int argc, char* argv[])
{
  char c = '??='; // OK, trigraph within single quotes
  printf("???\n"); // OK, not a trigraph
  printf("??=\n"); // NOK
  printf("??/\n"); // NOK
  printf("??'\n"); // NOK
  printf("??(\n"); // NOK
  printf("??)\n"); // NOK
  printf("??!\n"); // NOK
  printf("??<\n"); // NOK
  printf("??>\n"); // NOK
  printf("??-\n"); // NOK
  printf("??"); // OK, not a trigraph
}

#include <stdio.h>
#include <stdlib.h>

int function_prototype(char*);

int function1()
{
  return 3;
}

void function2()
{
  function1();
}

int function3(char* ptr) /* NOK */
{
  if (ptr == NULL) return -1;
  
  return 7;
}

void function4(char *ptr) /* NOK */
{
  if (0) return;
  
  printf("hello world!\n");
}

int main(int argc, char* argv[])
{
  function1();
  function2();
  function3("foobar");
  function4("john");
  
  return 0;
}

#include <stdio.h>
#include <errno.h>

typedef struct my_struct_struct {
  int a;
} my_struct_type;

my_struct_type foobar;

int errno; /* not detected, because header files are not fully processed yet, CAUTION: errno is usually defined as a macro! */
int fileA;

void my_function(int fileA) /* NOK, fileA at 11 */
{

}

void function_prototype_1(int arg1, int arg2);
void function_prototype_2(int arg1, int arg2);

int main(int argc, char* argv[])
{
  void function_prototype_3(int arg1, int arg2);
  void function_prototype_4(int arg1, int arg2);

  int a;
  int b;
  
  int errno; /* NOK, errno at 10 */
  
  {
    typedef int fileA; /* NOK, fileA at 11 */
  }
  
  my_function(42);

  union {
    int a; /* this is ok */
  } my_union;
  
  union my_union {
    int b;
  } fileA; /* NOK, fileA at 11 */
  
  {
    union my_union a; /* NOK, a at 20 */
  }
  
  {
    enum
    {
      HAHAHA,
      LOOOOO
    } a; /* NOK, a at 20 */
  }    
  
  struct my_struct_struct foobar; /* NOK, foobar at 8 */
  
  {
    struct {
      int my_int;
    } a; /* NOK, a at 26 */
    
    a.my_int = 12;
    
    int *foobar; /* NOK, foobar at 8 */
    int fileA[10]; /* NOK, fileA at 11 */
  }
  
  {
    int a; /* NOK, a at 26 */
    int fileA; /* NOK, fileA at 11 */
  }
  
  switch (0)
  {
    int x = 123; /* x is not initialized (unreachable code), but it *is* declared! */
    case 0:
      printf("case 0, x = %d\n", x); /* x is visible here, but uninitialized */
      int b; /* NOK, C99, b at 27 */
      int c;
  }
  
  /* x is invisible here */
  int x = 0;
  
  for (int i = 0; i < 3; i++)
  {
    printf("i = %d\n", i);
  }
  
  for (int i = 0; i < 3; i++)
  {
    int i = 0; /* NOK, i at 92 */ /* THIS IS NOT A REDECLARATION! The for loop creates a scope, and the compound statement an inner one */
    break;
  }
  
  for (;;)
  {
    break;
  }
 
  /* i is invisible here */
  
  a = 0;
  while (a < 3)
  {
    int a; /* NOK, a at 26 */
    int z;
    printf("a = %d\n", a);
    a++;
    if (a > 10) break; /* the condition of the while is evaluated using the outer a */
  }
  
  /* The 2 following lines are actually failing, limitation of our solution */
  int argc; /* NOK, argc at 25 */
  int argv; /* NOK, argv at 25 */
  
  return 0;
}

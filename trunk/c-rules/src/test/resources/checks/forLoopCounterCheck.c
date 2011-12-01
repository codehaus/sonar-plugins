#include <stdio.h>
#include <stdbool.h>

int myfunction(void)
{
  return 0;
}

int main(int argc, char* argv[])
{  
  int x, y, z;
  int *ptr;
  
  union my_union {
    int i;
  } my_union_var;
  
  union my_union *my_union_ptr = &my_union_var;
  int a[1];
  
  for (;;) break;
  for (x = 0; x < 10; x++) break;
  for (; x < 10; x++) break;
  for (x = 0; x >= 10; x++) break;
  for (x = 0; (x >= 10 && !y) || z; x++) break;
  for (y = 0; x < 10; x++) break; /* NOK */
  for (x = 0; x == 10; x++) break; /* NOK */
  for (x = 0; x = 10; x++) break; /* NOK */
  for (x = 0; x < 10 && *ptr; x++) break; /* NOK */
  for (x = 0; &x < 10; x++) break; /* NOK */
  for (my_union_var.i = 0; my_union_var.i < 10; my_union_var.i++) break; /* NOK */
  for (my_union_ptr->i = 0; my_union_ptr->i < 10; my_union_ptr->i++) break; /* NOK */
  for (a[0] = 0; a[0] < 10; a[0]++) break; /* NOK */
  for (x = 0; x < 10 && myfunction(); x++) break; /* NOK */
  for (x = 0; x < 10; x+= myfunction()) break; /* NOK */
  
  return 0;
}

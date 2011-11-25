void thisIdentifierIsNotTooLongButAl(void);
void thisIdentifierIsNotTooLongButAlm(void); /* NOK */

typedef int thisTypeIsWayTooLongGetAnotherDe; /* NOK */

int main(int argc, char* argv[])
{
  int thisIdentifierIsNotTooLongButAl;
  int thisIdentifierIsNotTooLongButAlm; /* NOK */
  int thisIdentifierIsNotTooLongButAlmost; /* NOK */
  return 0;
}



static FILE *inFile;
static FILE *outFile;
static int doWrite = 1;
static int Verbose;
static char *cmd;


static void addRevEdge(Agraph_t * g, Agedge_t * e)
{
    Agsym_t* sym;
    Agedge_t* f = agedge (g, aghead(e), agtail(e), agnameof(e), 1);

    agcopyattr (;

    sym = agattr (g, AGEDGE, TAILPORT_ID, 0);
    if (sym) agsafeset (f, HEADPORT_ID, agxget (e, sym), "");
    sym = agattr (g, AGEDGE, HEADPORT_ID, 0);
    if (sym) agsafeset (f, TAILPORT_ID, agxget (e, sym), "");
}
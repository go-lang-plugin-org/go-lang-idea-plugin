package main
const (
      ET_NONE   Type = 0      /* Unknown type. */
      ET_REL    Type = 1      /* Relocatable. */
      ET_EXEC   Type = 2      /* Executable. */
      ET_DYN    Type = 3      /* Shared object. */
      ET_CORE   Type = 4      /* Core file. */
      ET_LOOS   Type = 0xfe00 /* First operating system specific. */
      ET_HIOS   Type = 0xfeff /* Last operating system-specific. */
      ET_LOPROC Type = 0xff00 /* First processor-specific. */
      ET_HIPROC Type = 0xffff /* Last processor-specific. */
)

#include "BOOT_ManageInterrupts.h"
#include "BOOT_FailMode.h"
#include "BOOT_TCAHW_Decrementer.h"
#include "BOOT_TCAHW_FPU.h"
#include "BOOT_TCAHW_L2SRAM.h"
#include "BOOT_TCAHW_Megatice.h"
#include "BOOT_TCAHW_MMU.h"
#include "BOOT_TCAHW_SDRAM.h"
#include "LIBBSP_ECCMEM.h"
#include "LIBBSP_CCSR.h"

 void ExternalInputInterruptHandler(void) {    uint32_t v_HighestPendingInterruptVector;    uint32_t v_regAddr;    v_regAddr = C_PIC_REG_IACK_ADDRESS;    v_HighestPendingInterruptVector = ((volatile tu_PIC_IACK*)v_regAddr)->Fields.VECTOR;     v_regAddr = C_PIC_REG_EOI_ADDRESS;    ((volatile tu_PIC_EOI*)v_regAddr)->Fields.EOI_CODE = 0ul;     if((SchedulingIT_TestUnderway) && (v_HighestPendingInterruptVector == C_BMI_SCHEDULING_IT_VECTOR))    {       MEGATICE_ExpectedExternalIT_Raised = TRUE;    }    else if((L2SRAM_ECC_TestUnderway) && (v_HighestPendingInterruptVector == C_BMI_ERROR_INTERNAL_IT_VECTOR))    {       LIBBSP_ECCMEM_InitializeL2S(E_LIBBSP_ECCMEM_MBU_SEU_EN, E_LIBBSP_ECCMEM_MBU_SEU_IT_DIS, (uint8_t)255);       L2SRAM_ECC_ExpectedExternalIT_Raised = TRUE;    }    else if((SDRAM_ECC_TestUnderway) && (v_HighestPendingInterruptVector == C_BMI_ERROR_INTERNAL_IT_VECTOR))    {       LIBBSP_ECCMEM_InitializeDDR(E_LIBBSP_ECCMEM_MBU_SEU_EN, E_LIBBSP_ECCMEM_MBU_SEU_IT_DIS, (uint8_t)255);       SDRAM_ECC_ExpectedExternalIT_Raised = TRUE;    }    else    {       BOOT_FailMode();    } }

package com.pesa.controller;

import com.pesa.common.api.ApiResponse;
import com.pesa.common.api.ApiResponses;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/reference")
public class ReferenceController {

    @GetMapping("/banks")
    public ResponseEntity<ApiResponse<?>> getBanks() {
        List<BankDto> banks = getBankList();
        return ResponseEntity.ok(ApiResponses.success("Banks retrieved", banks));
    }

    @GetMapping("/banks/{bankId}/branches")
    public ResponseEntity<ApiResponse<?>> getBankBranches(@PathVariable int bankId) {
        List<BankBranchDto> branches = getBranchesForBank(bankId);
        if (branches.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponses.error("Bank not found"));
        }
        return ResponseEntity.ok(ApiResponses.success("Bank branches retrieved", branches));
    }

    private List<BankDto> getBankList() {
        List<BankDto> banks = new ArrayList<>();
        banks.add(BankDto.builder().id(1).name("National Bank of Commerce").code("NBC").build());
        banks.add(BankDto.builder().id(2).name("CRDB Bank").code("CRDB").build());
        banks.add(BankDto.builder().id(3).name("Tanzanian Bank").code("TBL").build());
        banks.add(BankDto.builder().id(4).name("Diamond Bank").code("DB").build());
        banks.add(BankDto.builder().id(5).name("Equity Bank").code("EB").build());
        banks.add(BankDto.builder().id(6).name("Exim Bank").code("EXIM").build());
        banks.add(BankDto.builder().id(7).name("ICT Bank").code("ICT").build());
        banks.add(BankDto.builder().id(8).name("Precision Bank").code("PB").build());
        return banks;
    }

    private List<BankBranchDto> getBranchesForBank(int bankId) {
        List<BankBranchDto> allBranches = new ArrayList<>();

        if (bankId == 1) {
            allBranches.add(BankBranchDto.builder().id(1).bankId(1).name("NBC Dar es Salaam Main").city("Dar es Salaam").build());
            allBranches.add(BankBranchDto.builder().id(2).bankId(1).name("NBC Dar es Salaam Upanga").city("Dar es Salaam").build());
            allBranches.add(BankBranchDto.builder().id(3).bankId(1).name("NBC Arusha").city("Arusha").build());
            allBranches.add(BankBranchDto.builder().id(4).bankId(1).name("NBC Dodoma").city("Dodoma").build());
        } else if (bankId == 2) {
            allBranches.add(BankBranchDto.builder().id(5).bankId(2).name("CRDB Dar es Salaam").city("Dar es Salaam").build());
            allBranches.add(BankBranchDto.builder().id(6).bankId(2).name("CRDB Arusha").city("Arusha").build());
            allBranches.add(BankBranchDto.builder().id(7).bankId(2).name("CRDB Moshi").city("Moshi").build());
        } else if (bankId == 3) {
            allBranches.add(BankBranchDto.builder().id(8).bankId(3).name("TBL Dar es Salaam").city("Dar es Salaam").build());
            allBranches.add(BankBranchDto.builder().id(9).bankId(3).name("TBL Dodoma").city("Dodoma").build());
        } else if (bankId == 4) {
            allBranches.add(BankBranchDto.builder().id(10).bankId(4).name("DB Dar es Salaam").city("Dar es Salaam").build());
            allBranches.add(BankBranchDto.builder().id(11).bankId(4).name("DB Arusha").city("Arusha").build());
        } else if (bankId == 5) {
            allBranches.add(BankBranchDto.builder().id(12).bankId(5).name("EB Dar es Salaam").city("Dar es Salaam").build());
        } else if (bankId == 6) {
            allBranches.add(BankBranchDto.builder().id(13).bankId(6).name("EXIM Dar es Salaam").city("Dar es Salaam").build());
        } else if (bankId == 7) {
            allBranches.add(BankBranchDto.builder().id(14).bankId(7).name("ICT Dar es Salaam").city("Dar es Salaam").build());
        } else if (bankId == 8) {
            allBranches.add(BankBranchDto.builder().id(15).bankId(8).name("PB Dar es Salaam").city("Dar es Salaam").build());
        }

        return allBranches;
    }

    @Data
    static class BankDto {
        private int id;
        private String name;
        private String code;

        public BankDto() {}

        public BankDto(int id, String name, String code) {
            this.id = id;
            this.name = name;
            this.code = code;
        }

        public static BankDtoBuilder builder() {
            return new BankDtoBuilder();
        }

        static class BankDtoBuilder {
            private int id;
            private String name;
            private String code;

            public BankDtoBuilder id(int id) {
                this.id = id;
                return this;
            }

            public BankDtoBuilder name(String name) {
                this.name = name;
                return this;
            }

            public BankDtoBuilder code(String code) {
                this.code = code;
                return this;
            }

            public BankDto build() {
                return new BankDto(id, name, code);
            }
        }
    }

    @Data
    static class BankBranchDto {
        private int id;
        private int bankId;
        private String name;
        private String city;

        public BankBranchDto() {}

        public BankBranchDto(int id, int bankId, String name, String city) {
            this.id = id;
            this.bankId = bankId;
            this.name = name;
            this.city = city;
        }

        public static BankBranchDtoBuilder builder() {
            return new BankBranchDtoBuilder();
        }

        static class BankBranchDtoBuilder {
            private int id;
            private int bankId;
            private String name;
            private String city;

            public BankBranchDtoBuilder id(int id) {
                this.id = id;
                return this;
            }

            public BankBranchDtoBuilder bankId(int bankId) {
                this.bankId = bankId;
                return this;
            }

            public BankBranchDtoBuilder name(String name) {
                this.name = name;
                return this;
            }

            public BankBranchDtoBuilder city(String city) {
                this.city = city;
                return this;
            }

            public BankBranchDto build() {
                return new BankBranchDto(id, bankId, name, city);
            }
        }
    }
}

package org.onoff18.contract.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Personal {

    // Личные данные
    private String name;
    private String passport;
    private String address;
    private String inn;
    private String npdRegistrationCity;
    private String phone;

    // Банковские реквизиты
    private String bankAccount;              // Расчетный счет
    private String bankName;                 // Название банка
    private String correspondentAccount;     // Корреспондентский счет
    private String bankIdentifierCode;       // БИК

}
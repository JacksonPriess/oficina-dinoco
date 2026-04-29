package com.dinoco.oficina.helper;

import com.dinoco.oficina.entity.Cliente;

public class ClienteBuilder {
    public static Cliente umCliente() {
        var cliente = new Cliente();
        return cliente;
    }
}

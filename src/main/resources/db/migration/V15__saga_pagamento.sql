CREATE TABLE os_pagamento_saga (
    saga_id UUID PRIMARY KEY,
    os_id BIGINT NOT NULL UNIQUE REFERENCES ordem_servico(id),
    valor NUMERIC(19,2) NOT NULL CHECK (valor > 0),
    estado VARCHAR(30) NOT NULL CHECK (estado IN (
        'SOLICITADA', 'PENDENTE', 'APROVADA', 'ESTORNO_SOLICITADO', 'ESTORNADA', 'ENTREGUE'
    )),
    pagamento_id UUID UNIQUE,
    provedor_id VARCHAR(100),
    estorno_id VARCHAR(100)
);

CREATE TABLE os_pagamento_outbox (
    event_id UUID PRIMARY KEY,
    saga_id UUID NOT NULL REFERENCES os_pagamento_saga(saga_id),
    tipo VARCHAR(50) NOT NULL,
    payload TEXT NOT NULL,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    publicado_em TIMESTAMPTZ,
    proxima_tentativa TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tentativas INTEGER NOT NULL DEFAULT 0,
    UNIQUE (saga_id, tipo)
);

CREATE INDEX idx_os_pagamento_outbox_pendente
    ON os_pagamento_outbox (proxima_tentativa, criado_em) WHERE publicado_em IS NULL;

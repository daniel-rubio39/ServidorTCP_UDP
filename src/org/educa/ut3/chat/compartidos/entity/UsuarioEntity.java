package org.educa.ut3.chat.compartidos.entity;

import java.io.Serializable;
import java.util.Objects;

public class UsuarioEntity implements Serializable {

    private final String nickname;

    public UsuarioEntity(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioEntity that = (UsuarioEntity) o;
        return Objects.equals(nickname, that.nickname);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(nickname);
    }
}

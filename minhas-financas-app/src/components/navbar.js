import React from "react";
import NavbarItem from "./navbarItem";

function Navbar() {
    return (
        <div className="navbar navbar-expand-lg fixed-top navbar-dark bg-primary">
            <div className="container">
                <a href="https://bootswatch.com/" className="navbar-brand">Minhas Finanças</a>
                <button className="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarResponsive" aria-controls="navbarResponsive" aria-expanded="false" aria-label="Toggle navigation">
                    <span className="navbar-toggler-icon"></span>
                </button>
                <div className="collapse navbar-collapse" id="navbarResponsive">
                    <ul className="navbar-nav">
                        <NavbarItem href="#/home" lable="Home" />
                        <NavbarItem href="#/cadastro-usuarios" lable="Usuários" />
                        <NavbarItem href="#/" lable="Lançamentos" />
                        <NavbarItem href="#/login" lable="Login" />
                    </ul>
                </div>
            </div>
        </div>
    )
}

export default Navbar
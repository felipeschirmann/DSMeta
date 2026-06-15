import logo from "../../assets/img/logo.svg";

import "./styles.css";

const logoImgProps = {
  src: logo,
  alt: "DSMeta",
  fetchPriority: "high",
  loading: "eager",
  decoding: "async"
} as unknown as React.ImgHTMLAttributes<HTMLImageElement>;

function Header() {
  return (
    <>
      <header>
        <div className="dsmeta-logo-container">
          <img {...logoImgProps} />
          <h1>DSMeta</h1>
          <p>
            Desenvolvido por{" "}
            <a href="https://github.com/felipeschirmann">@felipeschirmann</a>
          </p>
        </div>
      </header>
    </>
  );
}

export default Header;

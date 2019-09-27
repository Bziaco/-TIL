import React from "react"
import { Link } from "gatsby"

import Layout from "../components/layout"

const AboutPage = () => {
  return (
    <div>
      <Layout>
        <h1>About me</h1>
        <p>I currently teach full-time on Udemy.</p>
        <p>
          <Link to="/contact">Contact me.</Link>
        </p>
      </Layout>
    </div>
  )
}

export default AboutPage

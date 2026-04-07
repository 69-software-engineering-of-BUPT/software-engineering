<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Choose Role - TA Recruitment</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css" />
    <style>
        .role-page {
            min-height: 100vh;
            background: #f4f4f2;
            padding: 24px;
            box-sizing: border-box;
        }
        .role-shell {
            max-width: 1180px;
            margin: 0 auto;
            border: 1px solid #d8d3ca;
            border-radius: 28px;
            background: #f8f6f2;
            overflow: hidden;
            display: grid;
            grid-template-columns: 1.2fr 1fr;
            min-height: 680px;
        }
        .role-left {
            padding: 38px 40px;
            border-right: 1px solid #ddd8ce;
            background: linear-gradient(180deg, #f6f7f8 0%, #f2f1ee 100%);
        }
        .role-badge {
            width: 66px;
            height: 66px;
            border-radius: 20px;
            border: 1px solid #d6d0c6;
            display: grid;
            place-items: center;
            background: #f6f3ee;
            color: #6b5a48;
            font-size: 32px;
            font-weight: 700;
        }
        .role-tag {
            margin-top: 18px;
            border-radius: 16px;
            padding: 6px 12px;
            border: 1px solid #d8d2c9;
            display: inline-block;
            font-size: 12px;
            letter-spacing: 2px;
            color: #7f8894;
            background: #f6f4f0;
            text-transform: uppercase;
        }
        .role-left h1 {
            margin-top: 24px;
            margin-bottom: 12px;
            font-size: 56px;
            line-height: 1.05;
            color: #2f3742;
        }
        .role-left p {
            margin: 0;
            font-size: 32px;
            color: #626e7d;
            line-height: 1.5;
            max-width: 560px;
        }
        .role-right {
            padding: 42px 40px;
            display: flex;
            flex-direction: column;
            justify-content: center;
            gap: 20px;
        }
        .role-cards {
            display: grid;
            grid-template-columns: repeat(3, minmax(0, 1fr));
            gap: 18px;
        }
        .role-card {
            border: 1px solid #d8d2c9;
            border-radius: 24px;
            background: #f7f6f3;
            padding: 22px;
            display: flex;
            flex-direction: column;
            gap: 14px;
        }
        .role-chip {
            width: 52px;
            height: 52px;
            border-radius: 16px;
            border: 1px solid #d4d8db;
            background: #dbe4ec;
            color: #405a70;
            display: grid;
            place-items: center;
            font-weight: 700;
            font-size: 28px;
        }
        .role-card h3 {
            margin: 0;
            font-size: 48px;
            line-height: 1;
            color: #2f3742;
        }
        .role-card p {
            margin: 0;
            color: #647181;
            font-size: 18px;
            line-height: 1.45;
            min-height: 168px;
        }
        .role-card .chip-button {
            width: fit-content;
            background: #dce5ed;
            border-color: #cfd8e1;
            color: #4d687f;
            font-size: 24px;
            padding: 14px 18px;
        }
        .back-row {
            margin-top: 6px;
        }
        .back-row .chip-button {
            font-size: 28px;
            padding: 14px 24px;
        }
        @media (max-width: 1100px) {
            .role-shell { grid-template-columns: 1fr; }
            .role-left { border-right: none; border-bottom: 1px solid #ddd8ce; }
            .role-cards { grid-template-columns: 1fr; }
        }
    </style>
</head>
<body class="role-page">
<div class="role-shell">
    <section class="role-left">
        <div class="role-badge">TR</div>
        <span class="role-tag">Select Identity</span>
        <h1>Choose a role</h1>
        <p>Each role enters a different navigation structure while sharing the same product language.</p>
    </section>

    <section class="role-right">
        <div class="role-cards">
            <article class="role-card">
                <div class="role-chip">TA</div>
                <h3>TA</h3>
                <p>Browse open positions, upload a PDF CV, write a statement and follow status updates.</p>
                <a class="chip-button" href="${pageContext.request.contextPath}/ta/enter?userId=TA001">Enter as TA</a>
            </article>
            <article class="role-card">
                <div class="role-chip">MO</div>
                <h3>MO</h3>
                <p>Launch positions, review applicants, approve, reject or request more information.</p>
                <a class="chip-button" href="${pageContext.request.contextPath}/jsp/login.jsp?role=MO">Enter as MO</a>
            </article>
            <article class="role-card">
                <div class="role-chip">AD</div>
                <h3>AD</h3>
                <p>Manage accounts, monitor vacancies, trigger reminders and inspect operation logs.</p>
                <a class="chip-button" href="${pageContext.request.contextPath}/ad/accounts">Enter as AD</a>
            </article>
        </div>

        <div class="back-row">
            <a class="chip-button" href="${pageContext.request.contextPath}/">Back to welcome</a>
        </div>
    </section>
</div>
</body>
</html>

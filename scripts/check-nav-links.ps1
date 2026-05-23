$ErrorActionPreference = "Stop"

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$controllerDir = Join-Path $repoRoot "src/main/java/com/bupt/tarecruit/controller"
$webXmlPath = Join-Path $repoRoot "src/main/webapp/WEB-INF/web.xml"
$webappDir = Join-Path $repoRoot "src/main/webapp"
$jspDir = Join-Path $webappDir "jsp"
$jsDir = Join-Path $webappDir "js"

$servletRoutes = New-Object 'System.Collections.Generic.HashSet[string]'
$jspPaths = New-Object 'System.Collections.Generic.HashSet[string]'
$missing = New-Object System.Collections.Generic.List[object]

Get-ChildItem -Path $controllerDir -Recurse -Filter *.java | ForEach-Object {
    $content = Get-Content -LiteralPath $_.FullName -Raw -Encoding UTF8
    foreach ($match in [regex]::Matches($content, '@WebServlet\("([^"]+)"\)')) {
        [void]$servletRoutes.Add($match.Groups[1].Value)
    }
}

$webXmlContent = Get-Content -LiteralPath $webXmlPath -Raw -Encoding UTF8
foreach ($match in [regex]::Matches($webXmlContent, '<url-pattern>([^<]+)</url-pattern>')) {
    [void]$servletRoutes.Add($match.Groups[1].Value.Trim())
}

Get-ChildItem -Path $jspDir -Recurse -Filter *.jsp | ForEach-Object {
    $relative = $_.FullName.Substring($webappDir.Length).Replace('\', '/')
    [void]$jspPaths.Add($relative)
}

$patterns = @(
    'href\s*=\s*"\$\{pageContext\.request\.contextPath\}([^"]+)"',
    'href\s*=\s*"<%=\s*request\.getContextPath\(\)\s*%>([^"]+)"',
    'href\s*=\s*"<%=\s*contextPath\s*%>([^"]+)"',
    'action\s*=\s*"\$\{pageContext\.request\.contextPath\}([^"]+)"',
    'action\s*=\s*"<%=\s*request\.getContextPath\(\)\s*%>([^"]+)"',
    'action\s*=\s*"<%=\s*contextPath\s*%>([^"]+)"',
    "window\.location\.href\s*=\s*getContextPath\(\)\s*\+\s*'([^']+)'",
    "window\.location\.href\s*=\s*contextPath\s*\+\s*'([^']+)'",
    "fetch\(getContextPath\(\)\s*\+\s*'([^']+)'",
    "fetch\(contextPath\s*\+\s*'([^']+)'"
)

$scanFiles = @()
$scanFiles += Get-ChildItem -Path $jspDir -Recurse -Filter *.jsp
$scanFiles += Get-ChildItem -Path $jsDir -Recurse -Filter *.js

foreach ($file in $scanFiles) {
    $content = Get-Content -LiteralPath $file.FullName -Raw -Encoding UTF8
    foreach ($pattern in $patterns) {
        foreach ($match in [regex]::Matches($content, $pattern)) {
            $target = $match.Groups[1].Value.Split('?')[0]
            if (-not $target.StartsWith('/')) {
                continue
            }
            if ($target.StartsWith('/css/') -or $target.StartsWith('/js/') -or $target.StartsWith('/assets/')) {
                continue
            }
            if ($servletRoutes.Contains($target) -or $jspPaths.Contains($target)) {
                continue
            }
            $relativeSource = $file.FullName.Substring($repoRoot.Length + 1).Replace('\', '/')
            $missing.Add([PSCustomObject]@{
                Source = $relativeSource
                Target = $match.Groups[1].Value
            })
        }
    }
}

if ($missing.Count -gt 0) {
    Write-Host "Broken internal navigation targets detected:" -ForegroundColor Red
    $missing |
        Sort-Object Source, Target -Unique |
        ForEach-Object { Write-Host (" - {0} -> {1}" -f $_.Source, $_.Target) }
    exit 1
}

Write-Host "Navigation link audit passed: all discovered internal targets resolve to a servlet route or JSP." -ForegroundColor Green
